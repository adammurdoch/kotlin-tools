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
    fun `can read then write`() {
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
    fun `can write then read`() {
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
    fun `can update`() {
        withStore { store ->
            val value = store.keyValue<String, Int>("value")
            value.set("a", 12)
            value.set("b", 44)
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.keyValue<String, Int>("value")

            value.set("a", 11)
            assertEquals(11, value.get("a"))
            assertEquals(listOf("a" to 11, "b" to 44), value.entries())

            assertEquals(listOf("value"), store.values())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.keyValue<String, Int>("value")
            assertEquals(11, value.get("a"))
            assertEquals(listOf("a" to 11, "b" to 44), value.entries())
        }
    }

    @Test
    fun `can remove`() {
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
    fun `can remove then update`() {
        withStore { store ->
            val value = store.keyValue<String, Int>("value")
            value.set("a", 12)
            value.set("b", 44)
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.keyValue<String, Int>("value")

            value.remove("a")
            assertNull(value.get("a"))
            assertEquals(listOf("b" to 44), value.entries())

            value.set("a", 11)
            assertEquals(11, value.get("a"))
            assertEquals(listOf("b" to 44, "a" to 11), value.entries())

            assertEquals(listOf("value"), store.values())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.keyValue<String, Int>("value")
            assertEquals(11, value.get("a"))
            assertEquals(listOf("b" to 44, "a" to 11), value.entries())
        }
    }

    @Test
    fun `can discard all entries`() {
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

    @Test
    fun `can read and write multiple values in same session`() {
        withStore { store ->
            val value1 = store.keyValue<String, Int>("value 1")
            assertNull(value1.get("a"))
            val value2 = store.keyValue<String, String>("value 2")
            assertNull(value2.get("a"))

            value1.set("a", 123)
            assertEquals(123, value1.get("a"))

            value2.set("a", "value 2")
            assertEquals("value 2", value2.get("a"))

            assertEquals(listOf("value 1", "value 2"), store.values())
        }

        withStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
            val value1 = store.keyValue<String, Int>("value 1")
            val value2 = store.keyValue<String, String>("value 2")
            assertEquals(123, value1.get("a"))
            assertEquals("value 2", value2.get("a"))
        }
    }
}