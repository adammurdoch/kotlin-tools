package net.rubygrapefruit.store

internal sealed interface StoreIndex {
    val hasValue: Boolean

    val visitorInfo: ContentVisitor.ValueInfo

    fun doDiscard()

    fun asValueStore(): StoredAddressIndex

    fun asKeyValueStore(): StoredAddressMapIndex
}

internal interface StoredAddress : StoreIndex {
    val data: DataFile
    fun get(): Address?
    fun set(address: Address)
    fun discard()
}

internal interface StoredAddressIndex: StoredAddress, StoreIndex {
    fun doSet(address: Address)
}

internal interface StoredAddressMap : StoreIndex {
    val data: DataFile
    fun get(): Map<String, Address>
    fun set(key: String, value: Address)
    fun remove(key: String)
    fun discard()
}

internal interface StoredAddressMapIndex: StoredAddressMap, StoreIndex {
    fun doSet(key: String, address: Address)
    fun doRemove(key: String)
}

internal interface ChangeLog {
    fun append(change: StoreChange)
}