@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

internal class Index(
    private val fileManager: FileManager,
    private val metadataFile: MetadataFile,
    private val maxChanges: Int
) : AutoCloseable {
    private var log = fileManager.logFile(metadataFile.currentGeneration)
    private var data = fileManager.dataFile(metadataFile.currentGeneration)
    private val changeLog = object : ChangeLog {
        override fun append(change: StoreChange) {
            doAppend(change)
        }
    }
    private var compacting = false
    private val entries: MutableMap<String, StoreIndex>
    private var changes: Int

    init {
        val content = readIndex()
        entries = content.entries
        changes = content.updates
    }

    override fun close() {
        log.close()
        data.close()
    }

    fun value(name: String): StoredBlock {
        val index = entries.getOrPut(name) {
            val storeId = StoreId(entries.size)
            doAppend(NewValueStore(storeId, name))
            DefaultStoredBlock(name, storeId, changeLog, data)
        }
        return index.asValueStore()
    }

    fun map(name: String): StoredBlockMap {
        val index = entries.getOrPut(name) {
            val storeId = StoreId(entries.size)
            doAppend(NewKeyValueStore(storeId, name))
            DefaultStoredBlockMap(name, storeId, changeLog, data)
        }
        return index.asKeyValueStore()
    }

    fun accept(visitor: ContentVisitor) {
        visitor.index(changes)
        for (entry in effectiveEntries().entries.sortedBy { it.key }) {
            visitor.value(entry.key, entry.value.visitorInfo)
        }
    }

    private fun effectiveEntries(): Map<String, StoreIndex> {
        return entries.filter { it.value.hasValue }
    }

    private fun doAppend(change: StoreChange) {
        if (changes < maxChanges || compacting) {
            println("-> APPEND $change, CHANGES = $changes")
            changes++
            log.append(change)
        } else {
            println("-> APPEND $change, COMPACTING")
            compacting = true
            fileManager.discard(log)
            val oldData = data
            val generation = metadataFile.currentGeneration + 1
            log = fileManager.logFile(generation)
            data = fileManager.dataFile(generation)
            changes = 0
            for (index in entries.values) {
                when (index) {
                    is StoredBlockIndex -> doAppend(NewValueStore(index.storeId, index.name))
                    is StoredBlockMapIndex -> doAppend(NewKeyValueStore(index.storeId, index.name))
                }
                index.replay(data)
            }
            metadataFile.updateGeneration(generation)
            fileManager.discard(oldData)
            compacting = false
            println("-> FINISH COMPACTING")
        }
    }

    private fun readIndex(): InitialContent {
        val byId = mutableMapOf<StoreId, StoreIndex>()
        val entries = mutableMapOf<String, StoreIndex>()
        var updates = 0

        log.read { change ->
            updates++
            when (change) {
                is NewValueStore -> {
                    val index = DefaultStoredBlock(change.name, change.store, changeLog, data)
                    byId[change.store] = index
                    entries[change.name] = index
                }

                is NewKeyValueStore -> {
                    val index = DefaultStoredBlockMap(change.name, change.store, changeLog, data)
                    byId[change.store] = index
                    entries[change.name] = index
                }

                is DiscardStore -> {
                    val index = byId.getValue(change.store)
                    index.doDiscard()
                }

                is SetValue -> {
                    val index = byId.getValue(change.store)
                    index.asValueStore().doSet(change.value)
                }

                is SetEntry -> {
                    val index = byId.getValue(change.store)
                    index.asKeyValueStore().doSet(change.key, change.value)
                }

                is RemoveEntry -> {
                    val index = byId.getValue(change.store)
                    index.asKeyValueStore().doRemove(change.key)
                }
            }
        }
        return InitialContent(updates, entries)
    }

    private class InitialContent(
        val updates: Int,
        val entries: MutableMap<String, StoreIndex>
    )
}