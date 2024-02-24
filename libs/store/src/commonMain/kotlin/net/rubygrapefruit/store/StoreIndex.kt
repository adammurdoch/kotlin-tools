package net.rubygrapefruit.store

internal sealed interface StoreIndex {
    val name: String

    val storeId: StoreId

    val hasValue: Boolean

    val visitorInfo: ContentVisitor.ValueInfo

    fun doDiscard()

    fun asValueStore(): StoredBlockIndex

    fun asKeyValueStore(): StoredBlockMapIndex

    fun replay(log: ChangeLog, data: DataFile)
}

internal interface StoredBlockIndex : StoredBlock, StoreIndex {
    fun doSet(block: Block)
}

internal interface StoredBlockMapIndex : StoredBlockMap, StoreIndex {
    fun doSet(key: String, block: Block)
    fun doRemove(key: String)
}

internal interface ChangeLog {
    fun appendBatch(change: StoreChange)

    fun append(change: StoreChange)
}