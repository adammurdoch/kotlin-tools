package net.rubygrapefruit.store

internal sealed interface StoreIndex {
    val hasValue: Boolean

    val visitorInfo: ContentVisitor.ValueInfo

    fun doDiscard()

    fun asValueStore(): StoredAddressIndex

    fun asKeyValueStore(): StoredAddressMapIndex
}

internal interface StoredAddressIndex : StoredAddress, StoreIndex {
    fun doSet(address: Address)
}

internal interface StoredAddressMapIndex : StoredAddressMap, StoreIndex {
    fun doSet(key: String, address: Address)
    fun doRemove(key: String)
}

internal interface ChangeLog {
    fun append(change: StoreChange)
}