@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import net.rubygrapefruit.file.regularFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class StoreTest : AbstractStoreTest() {
    @Test
    fun `cannot open when store file has incorrect version`() {
        val files = listOf(StoreFile.Index, StoreFile.Data)

        for (file in files) {
            withStore {
                it.value<String>("value").set("ok")
            }

            val storeFile = testStore.file(file.name)
            assertTrue(storeFile.metadata().regularFile)

            storeFile.withContent {
                // Overwrite the first byte
                it.writeStream.write(byteArrayOf(1))
            }

            try {
                Store.open(testStore)
                fail()
            } catch (e: IllegalStateException) {
                assertEquals("Unexpected version in file.", e.message)
            }
        }
    }

    @Test
    fun `can discard store contents on open`() {
        withStore { store ->
            store.value<Long>("long").set(123)
            store.keyValue<Long, String>("longs").set(123, "value")
        }

        Store.open(testStore, discard = true).use { store ->
            assertTrue(store.values().isEmpty())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
        }
    }

    @Test
    fun `cannot open key value store as a value store`() {
        withStore { store ->
            store.keyValue<String, String>("value").set("a", "value 1")

            try {
                store.value<String>("value")
                fail()
            } catch (e: IllegalArgumentException) {
                assertEquals("Cannot open key-value store 'value' as a value store.", e.message)
            }
        }
        withStore { store ->
            try {
                store.value<String>("value")
                fail()
            } catch (e: IllegalArgumentException) {
                assertEquals("Cannot open key-value store 'value' as a value store.", e.message)
            }
        }
    }

    @Test
    fun `cannot open value store as a key value store`() {
        withStore { store ->
            store.value<String>("value").set("value 1")

            try {
                store.keyValue<String, String>("value")
                fail()
            } catch (e: IllegalArgumentException) {
                assertEquals("Cannot open value store 'value' as a key-value store.", e.message)
            }
        }
        withStore { store ->
            try {
                store.keyValue<String, String>("value")
                fail()
            } catch (e: IllegalArgumentException) {
                assertEquals("Cannot open value store 'value' as a key-value store.", e.message)
            }
        }
    }
}