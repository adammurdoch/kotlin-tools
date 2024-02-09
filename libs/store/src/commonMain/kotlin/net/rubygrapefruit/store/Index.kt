@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import net.rubygrapefruit.file.FileContent
import net.rubygrapefruit.file.RegularFile
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
        val index = currentIndex.getOrPut(name) { DefaultValueStoreIndex() }
        return if (index is DefaultValueStoreIndex) {
            index
        } else {
            throw IllegalArgumentException("Cannot open key-value store '$name' as a value store.")
        }
    }

    fun keyValue(name: String): KeyValueStoreIndex {
        val index = currentIndex.getOrPut(name) { DefaultKeyValueStoreIndex() }
        return if (index is KeyValueStoreIndex) {
            index
        } else {
            throw IllegalArgumentException("Cannot open value store '$name' as a key-value store.")
        }
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
            encoder.ushort(version)
            encoder.ushort(codec.version)
            encoder.int(entries.size)
            for (indexEntry in entries.entries) {
                encoder.string(indexEntry.key)
                val value = indexEntry.value
                when (value) {
                    is DefaultValueStoreIndex -> {
                        encoder.ushort(1u)
                        encoder.long(value.address!!.offset)
                    }

                    is DefaultKeyValueStoreIndex -> {
                        encoder.ushort(2u)
                        encoder.int(value.entries.size)
                        for (valueEntry in value.entries.entries) {
                            encoder.string(valueEntry.key)
                            encoder.long(valueEntry.value.offset)
                        }
                    }
                }
            }
        }
    }

    private fun readIndex(content: FileContent, codec: SimpleCodec): MutableMap<String, IndexEntry> {
        return if (content.length() == 0L) {
            mutableMapOf()
        } else {
            content.seek(0)
            val decoder = codec.decoder(content.readStream)
            require(decoder.ushort() == version)
            require(decoder.ushort() == codec.version)
            val count = decoder.int()
            val result = LinkedHashMap<String, IndexEntry>(count)
            for (i in 0 until count) {
                val name = decoder.string()
                val tag = decoder.ushort()
                when (tag) {
                    1.toUShort() -> {
                        val address = Address(decoder.long())
                        result[name] = DefaultValueStoreIndex(address)
                    }

                    2.toUShort() -> {
                        val count = decoder.int()
                        val entries = LinkedHashMap<String, Address>(count)
                        for (i in 0 until count) {
                            val key = decoder.string()
                            val address = Address(decoder.long())
                            entries[key] = address
                        }
                        result[name] = DefaultKeyValueStoreIndex(entries)
                    }

                    else -> throw IllegalArgumentException()
                }
            }
            result
        }
    }

    private sealed class IndexEntry : ContentVisitor.ValueInfo {
        abstract val hasValue: Boolean
    }

    private inner class DefaultValueStoreIndex(
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
        val entries: MutableMap<String, Address> = mutableMapOf<String, Address>()
    ) : IndexEntry(), KeyValueStoreIndex {

        override val hasValue: Boolean
            get() = entries.isNotEmpty()

        override val formatted: String
            get() = "${entries.size} entries"

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