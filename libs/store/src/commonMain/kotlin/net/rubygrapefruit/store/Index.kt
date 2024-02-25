@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

internal class Index(
    private val fileManager: FileManager,
    private val metadataFile: MetadataFile,
    private val maxChanges: Int
) : AutoCloseable {
    private var log = fileManager.logFile(metadataFile.currentGeneration)
    private var data = fileManager.dataFile(metadataFile.currentGeneration)
    private var changeLog = DefaultChangeLog()
    private var compacting = false
    private val entries: MutableMap<String, StoreIndex>
    private var newChanges: Int

    init {
        val content = readIndex()
        entries = content.entries
        newChanges = content.changes - metadataFile.compactedChanges
    }

    override fun close() {
        metadataFile.updateNonCompactedChanges(newChanges)
        log.close()
        data.close()
    }

    fun value(name: String): StoredBlock {
        val index = entries.getOrPut(name) {
            val storeId = StoreId(entries.size)
            DefaultStoredBlock(name, storeId, changeLog, data)
        }
        return index.asValueStore()
    }

    fun map(name: String): StoredBlockMap {
        val index = entries.getOrPut(name) {
            val storeId = StoreId(entries.size)
            DefaultStoredBlockMap(name, storeId, changeLog, data)
        }
        return index.asKeyValueStore()
    }

    fun accept(visitor: ContentVisitor) {
        visitor.store(ContentVisitor.StoreInfo(metadataFile.compactedChanges + newChanges, metadataFile.currentGeneration))
        for (entry in effectiveEntries().entries.sortedBy { it.key }) {
            visitor.value(entry.key, entry.value.visitorInfo)
        }
    }

    private fun effectiveEntries(): Map<String, StoreIndex> {
        return entries.filter { it.value.hasValue }
    }

    private fun doAppend(change: StoreChange) {
        if (newChanges < maxChanges || compacting) {
            newChanges++
            log.append(change)
        } else {
            compacting = true
            val oldLog = log
            val oldData = data
            val generation = metadataFile.currentGeneration + 1
            log = fileManager.logFile(generation)
            data = fileManager.dataFile(generation)
            newChanges = 0
            for (index in entries.values) {
                index.replay(changeLog, data)
            }
            metadataFile.updateGeneration(generation, newChanges)
            fileManager.closeAndDelete(oldLog)
            fileManager.closeAndDelete(oldData)
            newChanges = 0
            compacting = false
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
                    val index = DefaultStoredBlock(change.name, change.store, changeLog, data, true)
                    byId[change.store] = index
                    entries[change.name] = index
                }

                is NewKeyValueStore -> {
                    val index = DefaultStoredBlockMap(change.name, change.store, changeLog, data, true)
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

    private inner class DefaultChangeLog : ChangeLog {
        override fun appendBatch(change: StoreChange) {
            val oldValue = compacting
            compacting = true
            doAppend(change)
            compacting = oldValue
        }

        override fun append(change: StoreChange) {
            doAppend(change)
        }
    }

    private class InitialContent(
        val changes: Int,
        val entries: MutableMap<String, StoreIndex>
    )
}