package net.rubygrapefruit.store

internal interface StoredBlockMap {
    val data: DataFile
    fun get(): Map<String, Block>
    fun set(key: String, value: Block)
    fun remove(key: String)
    fun discard()
}