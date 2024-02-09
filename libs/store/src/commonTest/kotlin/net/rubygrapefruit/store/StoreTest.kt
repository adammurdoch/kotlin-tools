@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class StoreTest : AbstractStoreTest() {
    @Test
    fun `can discard store contents on open`() {
        val dir = fixture.testDir

        Store.open(dir).use { store ->
            store.value<Long>("long").set(123)
            store.keyValue<Long, String>("longs").set(123, "value")
        }

        Store.open(dir, discard = true).use { store ->
            assertTrue(store.values().isEmpty())
        }

        Store.open(dir).use { store ->
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
            } catch (e: Exception) {
                assertEquals("Cannot open key-value store 'value' as a value store.", e.message)
            }
        }
        withStore { store ->
            try {
                store.value<String>("value")
                fail()
            } catch (e: Exception) {
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
            } catch (e: Exception) {
                assertEquals("Cannot open value store 'value' as a key-value store.", e.message)
            }
        }
        withStore { store ->
            try {
                store.keyValue<String, String>("value")
                fail()
            } catch (e: Exception) {
                assertEquals("Cannot open value store 'value' as a key-value store.", e.message)
            }
        }
    }
}