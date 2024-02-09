package net.rubygrapefruit.store

internal sealed interface StoreIndex {
}

internal interface ValueStoreIndex : StoreIndex {
    fun get(): Address?
    fun set(address: Address)
    fun discard()
}

internal interface KeyValueStoreIndex : StoreIndex {
    fun get(): Map<String, Address>
    fun set(key: String, value: Address)
    fun remove(key: String)
    fun discard()
}
