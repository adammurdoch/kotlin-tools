@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.SimpleCodec

internal class Index(
    indexFile: RegularFile,
    codec: SimpleCodec
) : AutoCloseable {
    private val index = IndexFile(indexFile, codec)
    private val entries: MutableMap<String, IndexEntry>
    private var changes: Int

    init {
        val content = readIndex()
        entries = content.entries
        changes = content.updates
    }

    override fun close() {
        index.close()
    }

    fun value(name: String): ValueStoreIndex {
        val index = entries.getOrPut(name) {
            val storeId = StoreId(entries.size)
            append(NewValueStore(storeId, name))
            DefaultValueStoreIndex(name, storeId)
        }
        return index.asValueStore()
    }

    fun keyValue(name: String): KeyValueStoreIndex {
        val index = entries.getOrPut(name) {
            val storeId = StoreId(entries.size)
            append(NewKeyValueStore(storeId, name))
            DefaultKeyValueStoreIndex(name, storeId)
        }
        return index.asKeyValueStore()
    }

    fun accept(visitor: ContentVisitor) {
        visitor.index(changes)
        for (entry in effectiveEntries().entries.sortedBy { it.key }) {
            visitor.value(entry.key, entry.value)
        }
    }

    private fun effectiveEntries(): Map<String, IndexEntry> {
        return entries.filter { it.value.hasValue }
    }

    private fun append(change: StoreChange) {
        changes++
        index.append(change)
    }

    private fun readIndex(): InitialContent {
        val byId = mutableMapOf<StoreId, IndexEntry>()
        val entries = mutableMapOf<String, IndexEntry>()
        var updates = 0

        index.read { change ->
            updates++
            when (change) {
                is NewValueStore -> {
                    val index = DefaultValueStoreIndex(change.name, change.store)
                    byId[change.store] = index
                    entries[change.name] = index
                }

                is NewKeyValueStore -> {
                    val index = DefaultKeyValueStoreIndex(change.name, change.store)
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

    private sealed class IndexEntry : ContentVisitor.ValueInfo {
        abstract val hasValue: Boolean

        abstract fun asValueStore(): DefaultValueStoreIndex

        abstract fun asKeyValueStore(): DefaultKeyValueStoreIndex

        abstract fun doDiscard()
    }

    private inner class DefaultValueStoreIndex(
        private val name: String,
        private val storeId: StoreId,
    ) : IndexEntry(), ValueStoreIndex {
        private var address: Address? = null

        override val hasValue: Boolean
            get() = address != null

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
            append(SetValue(storeId, address))
        }

        override fun discard() {
            doDiscard()
            append(DiscardStore(storeId))
        }

        override fun doDiscard() {
            this.address = null
        }

        fun doSet(address: Address) {
            this.address = address
        }
    }

    private inner class DefaultKeyValueStoreIndex(
        private val name: String,
        private val storeId: StoreId,
    ) : IndexEntry(), KeyValueStoreIndex {
        private val entries = mutableMapOf<String, Address>()

        override val hasValue: Boolean
            get() = entries.isNotEmpty()

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
            append(SetEntry(storeId, key, value))
        }

        override fun remove(key: String) {
            doRemove(key)
            append(RemoveEntry(storeId, key))
        }

        override fun discard() {
            doDiscard()
            append(DiscardStore(storeId))
        }

        override fun doDiscard() {
            entries.clear()
        }

        fun doSet(key: String, address: Address) {
            entries[key] = address
        }

        fun doRemove(key: String) {
            entries.remove(key)
        }
    }

    private class InitialContent(
        val updates: Int,
        val entries: MutableMap<String, IndexEntry>
    )
}