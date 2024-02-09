package net.rubygrapefruit.store

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KeyValueStoreTest : AbstractStoreTest() {
    @Test
    fun `can read from empty store`() {
        withStore { store ->
            assertTrue(store.values().isEmpty())

            val value = store.keyValue<String, Int>("value")
            assertNull(value.get("a"))

            // Not visible until it has been written to
            assertTrue(store.values().isEmpty())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
            val value = store.keyValue<String, Int>("value")
            assertNull(value.get("a"))
            assertTrue(store.values().isEmpty())
        }
    }
}