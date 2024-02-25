package net.rubygrapefruit.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StoredMapTest : AbstractStoreTest() {
    @Test
    fun `can read from empty store`() {
        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.map<String, Int>("value")
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())

            // Not visible until it has been written to
            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.map<String, Int>("value")
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())

            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }
    }

    @Test
    fun `can read then write`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            assertNull(value.get("a"))

            value.set("a", 12)
            assertEquals(12, value.get("a"))
            assertEquals(listOf("a" to 12), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())

            val value = store.map<String, Int>("value")
            assertEquals(12, value.get("a"))
            assertEquals(listOf("a" to 12), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
        }
    }

    @Test
    fun `can write then read`() {
        withStore { store ->
            val value = store.map<String, Int>("value")

            value.set("a", 12)
            assertEquals(12, value.get("a"))
            assertEquals(listOf("a" to 12), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())

            val value = store.map<String, Int>("value")
            assertEquals(12, value.get("a"))
            assertEquals(listOf("a" to 12), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
        }
    }

    @Test
    fun `can read then write multiple entries`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            assertNull(value.get("a"))
            assertNull(value.get("b"))

            value.set("a", 1)
            value.set("b", 2)
            assertEquals(1, value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("a" to 1, "b" to 2), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.changes())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.changes())

            val value = store.map<String, Int>("value")
            assertEquals(1, value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("a" to 1, "b" to 2), value.entries())
        }
    }

    @Test
    fun `can update an entry`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            value.set("a", 12)
            value.set("b", 44)
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.changes())

            val value = store.map<String, Int>("value")

            value.set("a", 11)
            assertEquals(11, value.get("a"))
            assertEquals(listOf("a" to 11, "b" to 44), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())

            val value = store.map<String, Int>("value")
            assertEquals(11, value.get("a"))
            assertEquals(listOf("a" to 11, "b" to 44), value.entries())
        }
    }

    @Test
    fun `can update an entry in multiple sessions`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            value.set("a", 12)
            value.set("b", 44)
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.changes())

            val value = store.map<String, Int>("value")

            value.set("a", 11)
            assertEquals(11, value.get("a"))
            assertEquals(listOf("a" to 11, "b" to 44), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())

            val value = store.map<String, Int>("value")

            value.set("a", 10)
            assertEquals(10, value.get("a"))
            assertEquals(listOf("a" to 10, "b" to 44), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.changes())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.changes())

            val value = store.map<String, Int>("value")
            assertEquals(10, value.get("a"))
            assertEquals(listOf("a" to 10, "b" to 44), value.entries())
        }
    }

    @Test
    fun `can update an entry many times in same session`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            value.set("a", 12)
            value.set("b", 44)
        }

        withMaxChanges(5) { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.changes())
            assertEquals(1, store.generation())

            val value = store.map<String, Int>("value")

            for (i in 1..2) {
                value.set("a", 100 + i)
                assertEquals(100 + i, value.get("a"))
                assertEquals(listOf("a" to 100 + i, "b" to 44), value.entries())

                assertEquals(listOf("value"), store.values())
                assertEquals(i + 3, store.changes())
            }

            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.changes())
            assertEquals(1, store.generation())

            for (i in 3..4) {
                value.set("a", 100 + i)
                assertEquals(100 + i, value.get("a"))
                assertEquals(listOf("a" to 100 + i, "b" to 44), value.entries())

                assertEquals(listOf("value"), store.values())
                assertEquals(i, store.changes())
                assertEquals(2, store.generation())
            }
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
            assertEquals(2, store.generation())

            val value = store.map<String, Int>("value")
            assertEquals(104, value.get("a"))
            assertEquals(listOf("a" to 104, "b" to 44), value.entries())
        }
    }

    @Test
    fun `can update an entry in a map with many entries`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            value.set("a", 1)
            value.set("b", 2)
            value.set("c", 3)
            value.set("d", 4)
            value.set("e", 5)
        }

        withMaxChanges(5) { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(6, store.changes())
            assertEquals(1, store.generation())

            val value = store.map<String, Int>("value")

            value.set("a", 100)
            assertEquals(100, value.get("a"))
            assertEquals(5, value.entries().size)

            assertEquals(listOf("value"), store.values())
            assertEquals(6, store.changes())
            assertEquals(2, store.generation())

            value.set("a", 101)
            assertEquals(101, value.get("a"))
            assertEquals(5, value.entries().size)

            assertEquals(listOf("value"), store.values())
            assertEquals(7, store.changes())
            assertEquals(2, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(7, store.changes())
            assertEquals(2, store.generation())

            val value = store.map<String, Int>("value")
            assertEquals(101, value.get("a"))
            assertEquals(5, value.entries().size)
        }
    }

    @Test
    fun `can remove an entry that does not exist`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            value.set("a", 1)
            value.set("b", 2)
        }

        withStore { store ->
            val value = store.map<String, Int>("value")
            assertEquals(3, store.changes())

            value.remove("c")
            assertNull(value.get("c"))
            assertEquals(1, value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("a" to 1, "b" to 2), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())

            val value = store.map<String, Int>("value")
            assertEquals(2, value.entries().size)
        }
    }

    @Test
    fun `can remove an entry from an empty map`() {
        withStore { store ->
            store.map<String, Int>("value")
        }

        withStore { store ->
            val value = store.map<String, Int>("value")
            assertEquals(0, store.changes())

            value.remove("c")
            assertNull(value.get("c"))
            assertTrue(value.entries().isEmpty())

            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())

            val value = store.map<String, Int>("value")
            assertTrue(value.entries().isEmpty())

            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
        }
    }

    @Test
    fun `can remove an entry`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            value.set("a", 1)
            value.set("b", 2)
        }

        withStore { store ->
            val value = store.map<String, Int>("value")
            assertEquals(3, store.changes())

            value.remove("a")
            assertNull(value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("b" to 2), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
        }

        withStore { store ->
            val value = store.map<String, Int>("value")
            assertNull(value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("b" to 2), value.entries())
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())

            value.remove("b")
            assertNull(value.get("a"))
            assertNull(value.get("b"))
            assertTrue(value.entries().isEmpty())
            assertTrue(store.values().isEmpty())
            assertEquals(5, store.changes())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(5, store.changes())
        }
    }

    @Test
    fun `can remove then update`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            value.set("a", 12)
            value.set("b", 44)
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.changes())

            val value = store.map<String, Int>("value")

            value.remove("a")
            assertNull(value.get("a"))
            assertEquals(listOf("b" to 44), value.entries())

            value.set("a", 11)
            assertEquals(11, value.get("a"))
            assertEquals(listOf("b" to 44, "a" to 11), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.changes())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.changes())

            val value = store.map<String, Int>("value")
            assertEquals(11, value.get("a"))
            assertEquals(listOf("b" to 44, "a" to 11), value.entries())
        }
    }

    @Test
    fun `can remove all entries`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            value.set("a", 1)
            value.set("b", 2)
            assertEquals(listOf("a" to 1, "b" to 2), value.entries())

            value.remove("b")
            value.remove("a")

            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())
            assertTrue(store.values().isEmpty())
            assertEquals(5, store.changes())
        }
        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(5, store.changes())

            val value = store.map<String, Int>("value")
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())
            assertTrue(store.values().isEmpty())
        }
    }

    @Test
    fun `can discard all entries`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            value.set("a", 1)
            value.set("b", 2)
            assertEquals(listOf("a" to 1, "b" to 2), value.entries())

            value.discard()
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())
            assertTrue(store.values().isEmpty())
            assertEquals(4, store.changes())
        }
        withStore { store ->
            assertTrue(store.values().isEmpty())

            val value = store.map<String, Int>("value")
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())
            assertTrue(store.values().isEmpty())
        }
    }

    @Test
    fun `can discard empty map`() {
        withStore { store ->
            val value = store.map<String, Int>("value")
            value.discard()
            assertTrue(value.entries().isEmpty())

            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())

            val value = store.map<String, Int>("value")
            assertTrue(value.entries().isEmpty())

            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
        }
    }

    @Test
    fun `can read and write multiple values in same session`() {
        withStore { store ->
            val value1 = store.map<String, Int>("value 1")
            assertNull(value1.get("a"))
            val value2 = store.map<String, String>("value 2")
            assertNull(value2.get("a"))

            value1.set("a", 123)
            assertEquals(123, value1.get("a"))

            value2.set("a", "value 2")
            assertEquals("value 2", value2.get("a"))

            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())
        }

        withStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())

            val value1 = store.map<String, Int>("value 1")
            val value2 = store.map<String, String>("value 2")
            assertEquals(123, value1.get("a"))
            assertEquals("value 2", value2.get("a"))
        }
    }
}