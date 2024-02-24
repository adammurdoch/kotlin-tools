package net.rubygrapefruit.store

internal interface StoredAddress {
    val data: DataFile
    fun get(): Address?
    fun set(address: Address)
    fun discard()
}