@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.io.codec.SimpleCodec

internal class Index(
    store: Directory,
    metadataFile: MetadataFile,
    codec: SimpleCodec,
    maxChanges: Int
) : AutoCloseable {
    private val index = IndexFile(store.file("log_${metadataFile.currentGeneration}.bin"), codec)
    private val data = DataFile(store.file("data_${metadataFile.currentGeneration}.bin"), codec)
    private val changeLog = object : ChangeLog {
        override fun append(change: StoreChange) {
            doAppend(change)
        }
    }
    private val entries: MutableMap<String, StoreIndex>
    private var changes: Int

    init {
        val content = readIndex()
        entries = content.entries
        changes = content.updates
    }

    override fun close() {
        index.close()
        data.close()
    }

    fun value(name: String): ValueStoreIndex {
        val index = entries.getOrPut(name) {
            val storeId = StoreId(entries.size)
            doAppend(NewValueStore(storeId, name))
            DefaultValueStoreIndex(name, storeId, changeLog, data)
        }
        return index.asValueStore()
    }

    fun keyValue(name: String): KeyValueStoreIndex {
        val index = entries.getOrPut(name) {
            val storeId = StoreId(entries.size)
            doAppend(NewKeyValueStore(storeId, name))
            DefaultKeyValueStoreIndex(name, storeId, changeLog, data)
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
        changes++
        index.append(change)
    }

    private fun readIndex(): InitialContent {
        val byId = mutableMapOf<StoreId, StoreIndex>()
        val entries = mutableMapOf<String, StoreIndex>()
        var updates = 0

        index.read { change ->
            updates++
            when (change) {
                is NewValueStore -> {
                    val index = DefaultValueStoreIndex(change.name, change.store, changeLog, data)
                    byId[change.store] = index
                    entries[change.name] = index
                }

                is NewKeyValueStore -> {
                    val index = DefaultKeyValueStoreIndex(change.name, change.store, changeLog, data)
                    byId[change.store] = index
                    entries[change.name] = index
                }

                is DiscardStore -> {
                    val index = byId.getValue(change.store)
                    index.doDiscard()
                }

                is SetValue -> {
                    val index = byId.getValue(change.store)
                    index.asValueStore().doSet(change.address)
                }

                is SetEntry -> {
                    val index = byId.getValue(change.store)
                    index.asKeyValueStore().doSet(change.key, change.address)
                }

                is RemoveEntry -> {
                    val index = byId.getValue(change.store)
                    index.asKeyValueStore().doRemove(change.key)
                }
            }
        }
        return InitialContent(updates, entries)
    }

    private class DefaultValueStoreIndex(
        private val name: String,
        private val storeId: StoreId,
        private val changeLog: ChangeLog,
        override val data: DataFile
    ) : ValueStoreIndex, ContentVisitor.ValueInfo {
        private var address: Address? = null

        override val hasValue: Boolean
            get() = address != null

        override val visitorInfo: ContentVisitor.ValueInfo
            get() = this

        override val formatted: String
            get() {
                val current = address
                return if (current == null) {
                    "no value"
                } else {
                    "0x" + current.offset.toHexString(HexFormat.UpperCase)
                }
            }

        override fun asValueStore(): DefaultValueStoreIndex {
            return this
        }

        override fun asKeyValueStore(): DefaultKeyValueStoreIndex {
            throw IllegalArgumentException("Cannot open value store '$name' as a key-value store.")
        }

        override fun get(): Address? {
            return address
        }

        override fun set(address: Address) {
            doSet(address)
            changeLog.append(SetValue(storeId, address))
        }

        override fun discard() {
            doDiscard()
            changeLog.append(DiscardStore(storeId))
        }

        override fun doDiscard() {
            this.address = null
        }

        override fun doSet(address: Address) {
            this.address = address
        }
    }

    private class DefaultKeyValueStoreIndex(
        private val name: String,
        private val storeId: StoreId,
        private val changeLog: ChangeLog,
        override val data: DataFile,
    ) : KeyValueStoreIndex, ContentVisitor.ValueInfo {
        private val entries = mutableMapOf<String, Address>()

        override val hasValue: Boolean
            get() = entries.isNotEmpty()

        override val visitorInfo: ContentVisitor.ValueInfo
            get() = this

        override val formatted: String
            get() = "${entries.size} entries"

        override fun asValueStore(): DefaultValueStoreIndex {
            throw IllegalArgumentException("Cannot open key-value store '$name' as a value store.")
        }

        override fun asKeyValueStore(): DefaultKeyValueStoreIndex {
            return this
        }

        override fun get(): Map<String, Address> {
            return entries
        }

        override fun set(key: String, value: Address) {
            doSet(key, value)
            changeLog.append(SetEntry(storeId, key, value))
        }

        override fun remove(key: String) {
            doRemove(key)
            changeLog.append(RemoveEntry(storeId, key))
        }

        override fun discard() {
            doDiscard()
            changeLog.append(DiscardStore(storeId))
        }

        override fun doDiscard() {
            entries.clear()
        }

        override fun doSet(key: String, address: Address) {
            entries[key] = address
        }

        override fun doRemove(key: String) {
            entries.remove(key)
        }
    }

    private class InitialContent(
        val updates: Int,
        val entries: MutableMap<String, StoreIndex>
    )
}