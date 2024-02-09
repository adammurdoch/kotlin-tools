package net.rubygrapefruit.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KeyValueStoreTest : AbstractStoreTest() {
    @Test
    fun `can read from empty store`() {
        withStore { store ->
            assertTrue(store.values().isEmpty())

            val value = store.keyValue<String, Int>("value")
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())

            // Not visible until it has been written to
            assertTrue(store.values().isEmpty())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())

            val value = store.keyValue<String, Int>("value")
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())

            assertTrue(store.values().isEmpty())
        }
    }

    @Test
    fun `can read from then write to store`() {
        withStore { store ->
            val value = store.keyValue<String, Int>("value")
            assertNull(value.get("a"))

            value.set("a", 12)
            assertEquals(12, value.get("a"))
            assertEquals(listOf("a" to 12), value.entries())

            assertEquals(listOf("value"), store.values())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.keyValue<String, Int>("value")
            assertEquals(12, value.get("a"))
            assertEquals(listOf("a" to 12), value.entries())
        }
    }

    @Test
    fun `can write to then read from store`() {
        withStore { store ->
            val value = store.keyValue<String, Int>("value")

            value.set("a", 12)
            assertEquals(12, value.get("a"))
            assertEquals(listOf("a" to 12), value.entries())

            assertEquals(listOf("value"), store.values())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.keyValue<String, Int>("value")
            assertEquals(12, value.get("a"))
            assertEquals(listOf("a" to 12), value.entries())
        }
    }

    @Test
    fun `can write multiple entries`() {
        withStore { store ->
            val value = store.keyValue<String, Int>("value")
            assertNull(value.get("a"))
            assertNull(value.get("b"))

            value.set("a", 1)
            value.set("b", 2)
            assertEquals(1, value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("a" to 1, "b" to 2), value.entries())

            assertEquals(listOf("value"), store.values())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.keyValue<String, Int>("value")
            assertEquals(1, value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("a" to 1, "b" to 2), value.entries())
        }
    }

    @Test
    fun `can remove an entry`() {
        withStore { store ->
            val value = store.keyValue<String, Int>("value")
            value.set("a", 1)
            value.set("b", 2)
        }

        withStore { store ->
            val value = store.keyValue<String, Int>("value")

            value.remove("a")
            assertNull(value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("b" to 2), value.entries())

            assertEquals(listOf("value"), store.values())
        }

        withStore { store ->
            val value = store.keyValue<String, Int>("value")
            assertNull(value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("b" to 2), value.entries())
            assertEquals(listOf("value"), store.values())

            value.remove("b")
            assertNull(value.get("a"))
            assertNull(value.get("b"))
            assertTrue(value.entries().isEmpty())
            assertTrue(store.values().isEmpty())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
        }
    }

    @Test
    fun `can discard value`() {
        withStore { store ->
            val value = store.keyValue<String, Int>("value")
            value.set("a", 1)
            value.set("b", 2)
            assertEquals(listOf("a" to 1, "b" to 2), value.entries())

            value.discard()
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())
            assertTrue(store.values().isEmpty())
        }
        withStore { store ->
            assertTrue(store.values().isEmpty())

            val value = store.keyValue<String, Int>("value")
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())
            assertTrue(store.values().isEmpty())
        }
    }
}