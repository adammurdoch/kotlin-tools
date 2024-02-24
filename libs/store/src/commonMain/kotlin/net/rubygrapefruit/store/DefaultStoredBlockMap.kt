package net.rubygrapefruit.store

internal class DefaultStoredBlockMap(
    override val name: String,
    override val storeId: StoreId,
    private var log: ChangeLog,
    override var data: DataFile,
    private var registered: Boolean = false
) : StoredBlockMapIndex, ContentVisitor.ValueInfo {
    private val entries = mutableMapOf<String, Block>()

    override val hasValue: Boolean
        get() = entries.isNotEmpty()

    override val visitorInfo: ContentVisitor.ValueInfo
        get() = this

    override val formatted: String
        get() = "${entries.size} entries"

    override fun asValueStore(): StoredBlockIndex {
        throw IllegalArgumentException("Cannot open stored map '$name' as a stored value.")
    }

    override fun asKeyValueStore(): StoredBlockMapIndex {
        return this
    }

    override fun get(): Map<String, Block> {
        return entries
    }

    override fun set(key: String, value: Block) {
        doSet(key, value)
        log.batch {
            if (!registered) {
                it.append(NewKeyValueStore(storeId, name))
                registered = true
            }
            it.append(SetEntry(storeId, key, value))
        }
    }

    override fun remove(key: String) {
        doRemove(key)
        if (registered) {
            log.append(RemoveEntry(storeId, key))
        }
    }

    override fun discard() {
        doDiscard()
        if (registered) {
            log.append(DiscardStore(storeId))
        }
    }

    override fun doDiscard() {
        entries.clear()
    }

    override fun doSet(key: String, block: Block) {
        entries[key] = block
    }

    override fun doRemove(key: String) {
        entries.remove(key)
    }

    override fun replay(log: ChangeLog, data: DataFile) {
        val oldEntries = LinkedHashMap(entries)
        val oldData = this.data
        registered = false
        this.log = log
        this.data = data
        if (oldEntries.isNotEmpty()) {
            for (entry in oldEntries) {
                val newBlock = data.copyFrom(oldData, entry.value)
                set(entry.key, newBlock)
            }
        }
    }
}