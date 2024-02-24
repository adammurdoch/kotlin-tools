@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

internal class DefaultStoredAddress(
    private val name: String,
    private val storeId: StoreId,
    private val changeLog: ChangeLog,
    override val data: DataFile
) : StoredAddressIndex, ContentVisitor.ValueInfo {
    private var address: Address? = null

    override val hasValue: Boolean
        get() = address != null

    override val visitorInfo: ContentVisitor.ValueInfo
        get() = this

    override val formatted: String
        get() {
            val current = address
            return if (current == null) {
                "no value"
            } else {
                "0x" + current.offset.toHexString(HexFormat.UpperCase)
            }
        }

    override fun asValueStore(): StoredAddressIndex {
        return this
    }

    override fun asKeyValueStore(): StoredAddressMapIndex {
        throw IllegalArgumentException("Cannot open stored value '$name' as a stored map.")
    }

    override fun get(): Address? {
        return address
    }

    override fun set(address: Address) {
        doSet(address)
        changeLog.append(SetValue(storeId, address))
    }

    override fun discard() {
        doDiscard()
        changeLog.append(DiscardStore(storeId))
    }

    override fun doDiscard() {
        this.address = null
    }

    override fun doSet(address: Address) {
        this.address = address
    }
}