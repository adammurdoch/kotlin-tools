@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import net.rubygrapefruit.file.FileContent
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.io.codec.Decoder
import net.rubygrapefruit.io.codec.Encoder
import net.rubygrapefruit.io.codec.SimpleCodec

internal class Index(
    index: RegularFile,
    private val codec: SimpleCodec
) : AutoCloseable {
    private val fileContent = index.openContent().successful()
    private val currentIndex = fileContent.using { readIndex(it, codec) }

    override fun close() {
        fileContent.close()
    }

    fun value(name: String): ValueStoreIndex {
        val index = currentIndex.getOrPut(name) { DefaultValueStoreIndex(name) }
        return index.asValueStore()
    }

    fun keyValue(name: String): KeyValueStoreIndex {
        val index = currentIndex.getOrPut(name) { DefaultKeyValueStoreIndex(name) }
        return index.asKeyValueStore()
    }

    fun accept(visitor: ContentVisitor) {
        for (entry in effectiveEntries().entries.sortedBy { it.key }) {
            visitor.value(entry.key, entry.value)
        }
    }

    private fun effectiveEntries(): Map<String, IndexEntry> {
        return currentIndex.filter { it.value.hasValue }
    }

    private fun writeIndex() {
        val entries = effectiveEntries()
        fileContent.using { content ->
            content.seek(0)
            val encoder = codec.encoder(content.writeStream)
            encoder.fileHeader(codec)
            encoder.int(entries.size)
            for (indexEntry in entries.entries) {
                encoder.string(indexEntry.key)
                when (val value = indexEntry.value) {
                    is DefaultValueStoreIndex -> {
                        encoder.ubyte(1u)
                        encoder.valueStoreIndex(value)
                    }

                    is DefaultKeyValueStoreIndex -> {
                        encoder.ubyte(2u)
                        encoder.keyValueStoreIndex(value)
                    }
                }
            }
        }
    }

    private fun Encoder.valueStoreIndex(value: DefaultValueStoreIndex) {
        long(value.address!!.offset)
    }

    private fun Encoder.keyValueStoreIndex(value: DefaultKeyValueStoreIndex) {
        int(value.entries.size)
        for (valueEntry in value.entries.entries) {
            string(valueEntry.key)
            long(valueEntry.value.offset)
        }
    }

    private fun readIndex(content: FileContent, codec: SimpleCodec): MutableMap<String, IndexEntry> {
        return if (content.length() == 0L) {
            mutableMapOf()
        } else {
            content.seek(0)
            val decoder = codec.decoder(content.readStream)
            decoder.checkFileHeader(codec)
            val count = decoder.int()
            val result = LinkedHashMap<String, IndexEntry>(count)
            for (i in 0 until count) {
                val name = decoder.string()
                val tag = decoder.ubyte()
                when (tag) {
                    1.toUByte() -> {
                        result[name] = decoder.valueStoreIndex(name)
                    }

                    2.toUByte() -> {
                        result[name] = decoder.keyValueStoreIndex(name)
                    }

                    else -> throw IllegalArgumentException()
                }
            }
            result
        }
    }

    private fun Decoder.valueStoreIndex(name: String): DefaultValueStoreIndex {
        val address = Address(long())
        val index = DefaultValueStoreIndex(name, address)
        return index
    }

    private fun Decoder.keyValueStoreIndex(name: String): DefaultKeyValueStoreIndex {
        val count = int()
        val entries = LinkedHashMap<String, Address>(count)
        for (i in 0 until count) {
            val key = string()
            val address = Address(long())
            entries[key] = address
        }
        val index = DefaultKeyValueStoreIndex(name, entries)
        return index
    }

    private sealed class IndexEntry : ContentVisitor.ValueInfo {
        abstract val hasValue: Boolean

        abstract fun asValueStore(): ValueStoreIndex

        abstract fun asKeyValueStore(): KeyValueStoreIndex
    }

    private inner class DefaultValueStoreIndex(
        private val name: String,
        var address: Address? = null
    ) : IndexEntry(), ValueStoreIndex {
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

        override fun asValueStore(): ValueStoreIndex {
            return this
        }

        override fun asKeyValueStore(): KeyValueStoreIndex {
            throw IllegalArgumentException("Cannot open value store '$name' as a key-value store.")
        }

        override fun get(): Address? {
            return address
        }

        override fun set(address: Address) {
            this.address = address
            writeIndex()
        }

        override fun discard() {
            this.address = null
            writeIndex()
        }
    }

    private inner class DefaultKeyValueStoreIndex(
        private val name: String,
        val entries: MutableMap<String, Address> = mutableMapOf()
    ) : IndexEntry(), KeyValueStoreIndex {

        override val hasValue: Boolean
            get() = entries.isNotEmpty()

        override val formatted: String
            get() = "${entries.size} entries"

        override fun asValueStore(): ValueStoreIndex {
            throw IllegalArgumentException("Cannot open key-value store '$name' as a value store.")
        }

        override fun asKeyValueStore(): KeyValueStoreIndex {
            return this
        }

        override fun get(): Map<String, Address> {
            return entries
        }

        override fun set(key: String, value: Address) {
            entries[key] = value
            writeIndex()
        }

        override fun remove(key: String) {
            entries.remove(key)
            writeIndex()
        }

        override fun discard() {
            entries.clear()
            writeIndex()
        }
    }
}