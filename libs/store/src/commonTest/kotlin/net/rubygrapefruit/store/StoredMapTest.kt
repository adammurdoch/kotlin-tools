@file:OptIn(ExperimentalStdlibApi::class)

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

            val value = store.map<String, Int>("value")
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())

            // Not visible until it has been written to
            assertTrue(store.values().isEmpty())
            assertEquals(1, store.indexChanges())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(1, store.indexChanges())

            val value = store.map<String, Int>("value")
            assertNull(value.get("a"))
            assertTrue(value.entries().isEmpty())

            assertTrue(store.values().isEmpty())
            assertEquals(1, store.indexChanges())
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
            assertEquals(2, store.indexChanges())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.indexChanges())

            val value = store.map<String, Int>("value")
            assertEquals(12, value.get("a"))
            assertEquals(listOf("a" to 12), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.indexChanges())
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
            assertEquals(2, store.indexChanges())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.indexChanges())

            val value = store.map<String, Int>("value")
            assertEquals(12, value.get("a"))
            assertEquals(listOf("a" to 12), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.indexChanges())
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
            assertEquals(3, store.indexChanges())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.indexChanges())

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
            assertEquals(3, store.indexChanges())

            val value = store.map<String, Int>("value")

            value.set("a", 11)
            assertEquals(11, value.get("a"))
            assertEquals(listOf("a" to 11, "b" to 44), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.indexChanges())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.indexChanges())

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
            assertEquals(3, store.indexChanges())

            val value = store.map<String, Int>("value")

            value.set("a", 11)
            assertEquals(11, value.get("a"))
            assertEquals(listOf("a" to 11, "b" to 44), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.indexChanges())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.indexChanges())

            val value = store.map<String, Int>("value")

            value.set("a", 10)
            assertEquals(10, value.get("a"))
            assertEquals(listOf("a" to 10, "b" to 44), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.indexChanges())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.indexChanges())

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

        Store.open(testStore, maxChanges = 5).use { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.indexChanges())

            val value = store.map<String, Int>("value")

            for (i in 1..2) {
                value.set("a", 100 + i)
                assertEquals(100 + i, value.get("a"))
                assertEquals(listOf("a" to 100 + i, "b" to 44), value.entries())

                assertEquals(listOf("value"), store.values())
                assertEquals(i + 3, store.indexChanges())
            }

            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.indexChanges())

            for (i in 3..4) {
                value.set("a", 100 + i)
                assertEquals(100 + i, value.get("a"))
                assertEquals(listOf("a" to 100 + i, "b" to 44), value.entries())

                assertEquals(listOf("value"), store.values())
                assertEquals(i, store.indexChanges())
            }
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.indexChanges())

            val value = store.map<String, Int>("value")
            assertEquals(104, value.get("a"))
            assertEquals(listOf("a" to 104, "b" to 44), value.entries())
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
            assertEquals(3, store.indexChanges())

            value.remove("a")
            assertNull(value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("b" to 2), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.indexChanges())
        }

        withStore { store ->
            val value = store.map<String, Int>("value")
            assertNull(value.get("a"))
            assertEquals(2, value.get("b"))
            assertEquals(listOf("b" to 2), value.entries())
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.indexChanges())

            value.remove("b")
            assertNull(value.get("a"))
            assertNull(value.get("b"))
            assertTrue(value.entries().isEmpty())
            assertTrue(store.values().isEmpty())
            assertEquals(5, store.indexChanges())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(5, store.indexChanges())
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
            assertEquals(3, store.indexChanges())

            val value = store.map<String, Int>("value")

            value.remove("a")
            assertNull(value.get("a"))
            assertEquals(listOf("b" to 44), value.entries())

            value.set("a", 11)
            assertEquals(11, value.get("a"))
            assertEquals(listOf("b" to 44, "a" to 11), value.entries())

            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.indexChanges())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.indexChanges())

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
            assertEquals(5, store.indexChanges())
        }
        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(5, store.indexChanges())

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
            assertEquals(4, store.indexChanges())
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
            assertEquals(4, store.indexChanges())
        }

        withStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.indexChanges())

            val value1 = store.map<String, Int>("value 1")
            val value2 = store.map<String, String>("value 2")
            assertEquals(123, value1.get("a"))
            assertEquals("value 2", value2.get("a"))
        }
    }
}