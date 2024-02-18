package net.rubygrapefruit.store

import net.rubygrapefruit.io.codec.Decoder
import net.rubygrapefruit.io.codec.Encoder

internal sealed class StoreChange

internal class NewValueStore(val store: StoreId, val name: String) : StoreChange()
internal class NewKeyValueStore(val store: StoreId, val name: String) : StoreChange()

internal class DiscardStore(val store: StoreId) : StoreChange()

internal class SetValue(val store: StoreId, val address: Address) : StoreChange()

internal class SetEntry(val store: StoreId, val key: String, val address: Address) : StoreChange()
internal class RemoveEntry(val store: StoreId, val key: String) : StoreChange()

private fun Encoder.storeId(store: StoreId) {
    int(store.id)
}

private fun Encoder.address(address: Address) {
    long(address.offset)
}

private val newValueStoreTag = 1u.toUByte()
private val newKeyValueStoreTag = 2u.toUByte()
private val discardStoreTag = 3u.toUByte()
private val setValueTag = 4u.toUByte()
private val setEntryTag = 5u.toUByte()
private val removeEntryTag = 6u.toUByte()

internal fun Encoder.encode(change: StoreChange) {
    when (change) {
        is NewValueStore -> {
            ubyte(newValueStoreTag)
            storeId(change.store)
            string(change.name)
        }

        is NewKeyValueStore -> {
            ubyte(newKeyValueStoreTag)
            storeId(change.store)
            string(change.name)
        }

        is DiscardStore -> {
            ubyte(discardStoreTag)
            storeId(change.store)
        }

        is SetValue -> {
            ubyte(setValueTag)
            storeId(change.store)
            address(change.address)
        }

        is SetEntry -> {
            ubyte(setEntryTag)
            storeId(change.store)
            string(change.key)
            address(change.address)
        }

        is RemoveEntry -> {
            ubyte(removeEntryTag)
            storeId(change.store)
            string(change.key)
        }
    }
}

private fun Decoder.storeId(): StoreId {
    return StoreId(int())
}

private fun Decoder.address(): Address {
    return Address(long())
}

internal fun Decoder.decode(): StoreChange {
    val tag = ubyte()
    return when (tag) {
        newValueStoreTag -> {
            val storeId = storeId()
            val name = string()
            NewValueStore(storeId, name)
        }

        newKeyValueStoreTag -> {
            val storeId = storeId()
            val name = string()
            NewKeyValueStore(storeId, name)
        }

        discardStoreTag -> {
            val storeId = storeId()
            DiscardStore(storeId)
        }

        setValueTag -> {
            val storeId = storeId()
            val address = address()
            SetValue(storeId, address)
        }

        setEntryTag -> {
            val storeId = storeId()
            val key = string()
            val address = address()
            SetEntry(storeId, key, address)
        }

        removeEntryTag -> {
            val storeId = storeId()
            val key = string()
            RemoveEntry(storeId, key)
        }

        else -> throw IllegalArgumentException()
    }
}