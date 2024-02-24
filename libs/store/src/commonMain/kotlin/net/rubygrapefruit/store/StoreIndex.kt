package net.rubygrapefruit.store

internal sealed interface StoreIndex {
    val hasValue: Boolean

    val visitorInfo: ContentVisitor.ValueInfo

    fun doDiscard()

    fun asValueStore(): ValueStoreIndex

    fun asKeyValueStore(): KeyValueStoreIndex
}

internal interface ValueStoreIndex : StoreIndex {
    val data: DataFile
    fun get(): Address?
    fun set(address: Address)
    fun discard()

    fun doSet(address: Address)
}

internal interface KeyValueStoreIndex : StoreIndex {
    val data: DataFile
    fun get(): Map<String, Address>
    fun set(key: String, value: Address)
    fun remove(key: String)
    fun discard()

    fun doSet(key: String, address: Address)
    fun doRemove(key: String)
}

internal interface ChangeLog {
    fun append(change: StoreChange)
}