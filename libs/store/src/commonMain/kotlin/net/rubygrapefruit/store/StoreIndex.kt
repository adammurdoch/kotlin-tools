package net.rubygrapefruit.store

internal sealed interface StoreIndex {
    val hasValue: Boolean

    val visitorInfo: ContentVisitor.ValueInfo

    fun doDiscard()

    fun asValueStore(): StoredBlockIndex

    fun asKeyValueStore(): StoredBlockMapIndex
}

internal interface StoredBlockIndex : StoredBlock, StoreIndex {
    fun doSet(block: Block)
}

internal interface StoredBlockMapIndex : StoredBlockMap, StoreIndex {
    fun doSet(key: String, block: Block)
    fun doRemove(key: String)
}

internal interface ChangeLog {
    fun append(change: StoreChange)
}