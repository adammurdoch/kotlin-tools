package net.rubygrapefruit.store

internal sealed interface StoreIndex {
}

internal interface ValueStoreIndex : StoreIndex {
    val data: DataFile
    fun get(): Address?
    fun set(address: Address)
    fun discard()
}

internal interface KeyValueStoreIndex : StoreIndex {
    val data: DataFile
    fun get(): Map<String, Address>
    fun set(key: String, value: Address)
    fun remove(key: String)
    fun discard()
}
