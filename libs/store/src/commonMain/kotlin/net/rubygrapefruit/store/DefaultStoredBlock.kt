@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

internal class DefaultStoredBlock(
    override val name: String,
    override val storeId: StoreId,
    private val changeLog: ChangeLog,
    override var data: DataFile
) : StoredBlockIndex, ContentVisitor.ValueInfo {
    private var block: Block? = null

    override val hasValue: Boolean
        get() = block != null

    override val visitorInfo: ContentVisitor.ValueInfo
        get() = this

    override val formatted: String
        get() {
            val current = block
            return if (current == null) {
                "no value"
            } else {
                val address = current.address.offset.toHexString(HexFormat.UpperCase)
                "0x$address (${current.size.value} bytes)"
            }
        }

    override fun asValueStore(): StoredBlockIndex {
        return this
    }

    override fun asKeyValueStore(): StoredBlockMapIndex {
        throw IllegalArgumentException("Cannot open stored value '$name' as a stored map.")
    }

    override fun get(): Block? {
        return block
    }

    override fun set(block: Block) {
        doSet(block)
        changeLog.append(SetValue(storeId, block))
    }

    override fun discard() {
        doDiscard()
        changeLog.append(DiscardStore(storeId))
    }

    override fun doDiscard() {
        this.block = null
    }

    override fun doSet(block: Block) {
        this.block = block
    }

    override fun replay(data: DataFile) {
        val current = block
        if (current != null) {
            val copy = data.copyFrom(this.data, current)
            set(copy)
        }
        this.data = data
    }
}