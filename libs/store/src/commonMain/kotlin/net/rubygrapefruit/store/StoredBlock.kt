package net.rubygrapefruit.store

internal interface StoredBlock {
    val data: DataFile
    fun get(): Block?
    fun set(block: Block)
    fun discard()
}