package net.rubygrapefruit.store

internal interface StoredAddressMap {
    val data: DataFile
    fun get(): Map<String, Address>
    fun set(key: String, value: Address)
    fun remove(key: String)
    fun discard()
}