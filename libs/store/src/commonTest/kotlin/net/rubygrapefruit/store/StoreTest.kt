package net.rubygrapefruit.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StoreTest : AbstractStoreTest() {
    @Test
    fun `cannot open key value store as a value store`() {
        withStore { store ->
            store.keyValue<String, String>("value").set("a", "value 1")

            try {
                store.value<String>("value")
                fail()
            } catch (e: Exception) {
                assertEquals("??", e.message)
            }
        }
        withStore { store ->
            try {
                store.value<String>("value")
                fail()
            } catch (e: Exception) {
                assertEquals("??", e.message)
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
                assertEquals("??", e.message)
            }
        }
        withStore { store ->
            try {
                store.keyValue<String, String>("value")
                fail()
            } catch (e: Exception) {
                assertEquals("??", e.message)
            }
        }
    }
}