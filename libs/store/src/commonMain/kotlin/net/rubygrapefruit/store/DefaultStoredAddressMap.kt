package net.rubygrapefruit.store

internal class DefaultStoredAddressMap(
    private val name: String,
    private val storeId: StoreId,
    private val changeLog: ChangeLog,
    override val data: DataFile,
) : StoredAddressMapIndex, ContentVisitor.ValueInfo {
    private val entries = mutableMapOf<String, Address>()

    override val hasValue: Boolean
        get() = entries.isNotEmpty()

    override val visitorInfo: ContentVisitor.ValueInfo
        get() = this

    override val formatted: String
        get() = "${entries.size} entries"

    override fun asValueStore(): StoredAddressIndex {
        throw IllegalArgumentException("Cannot open stored map '$name' as a stored value.")
    }

    override fun asKeyValueStore(): StoredAddressMapIndex {
        return this
    }

    override fun get(): Map<String, Address> {
        return entries
    }

    override fun set(key: String, value: Address) {
        doSet(key, value)
        changeLog.append(SetEntry(storeId, key, value))
    }

    override fun remove(key: String) {
        doRemove(key)
        changeLog.append(RemoveEntry(storeId, key))
    }

    override fun discard() {
        doDiscard()
        changeLog.append(DiscardStore(storeId))
    }

    override fun doDiscard() {
        entries.clear()
    }

    override fun doSet(key: String, address: Address) {
        entries[key] = address
    }

    override fun doRemove(key: String) {
        entries.remove(key)
    }
}