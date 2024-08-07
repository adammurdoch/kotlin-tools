package net.rubygrapefruit.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StoredValueTest : AbstractStoreTest() {
    @Test
    fun `can read from empty store`() {
        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")
            assertNull(value.get())

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

            val value = store.value<String>("value")
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }
    }

    @Test
    fun `can read then write value`() {
        withStore { store ->
            val value = store.value<String>("value")
            assertNull(value.get())

            value.set("value 1")
            assertEquals("value 1", value.get())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(2, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(2, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(2, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }
        withCompactedStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }
    }

    @Test
    fun `can write then read value`() {
        withStore { store ->
            val value = store.value<String>("value")

            value.set("value 1")
            assertEquals("value 1", value.get())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(2, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(2, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(2, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }
        withCompactedStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }
    }

    @Test
    fun `can write then discard value`() {
        withStore { store ->
            val value = store.value<String>("empty")
            value.set("value 1")
            assertEquals("value 1", value.get())

            value.discard()
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("empty")
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }
        withCompactedStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value = store.value<String>("empty")
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }
    }

    @Test
    fun `can discard value`() {
        withStore { store ->
            val value = store.value<String>("empty")
            value.set("value 1")
        }

        withStore { store ->
            val value = store.value<String>("empty")
            assertEquals("value 1", value.get())

            value.discard()
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("empty")
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }
        withCompactedStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value = store.value<String>("empty")
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }
    }

    @Test
    fun `can discard value that has not been written`() {
        withStore { store ->
            val value = store.value<String>("empty")
            value.discard()

            assertNull(value.get())

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

            val value = store.value<String>("empty")
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }
    }

    @Test
    fun `can discard value then write in same session`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("value 1")
        }

        withStore { store ->
            val value = store.value<String>("value")
            assertEquals("value 1", value.get())

            value.discard()
            assertNull(value.get())
            assertTrue(store.values().isEmpty())

            value.set("value 2")

            assertEquals("value 2", value.get())

            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 2", value.get())
        }
        withCompactedStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 2", value.get())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }
    }

    @Test
    fun `can discard value then write in different sessions`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("value 1")
        }

        withStore { store ->
            val value = store.value<String>("value")
            assertEquals("value 1", value.get())

            value.discard()
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }
        withStore { store ->
            val value = store.value<String>("value")
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            value.set("value 2")

            assertEquals("value 2", value.get())

            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 2", value.get())
        }
        withCompactedStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 2", value.get())
        }
    }

    @Test
    fun `can update value`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("value 1")
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(2, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())
            value.set("value 2")
            assertEquals("value 2", value.get())

            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 2", value.get())
        }
        withCompactedStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 2", value.get())
        }
    }

    @Test
    fun `can update value multiple times in same session`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("value 1")
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())
            value.set("value 2")
            value.set("value 3")
            assertEquals("value 3", value.get())

            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 3", value.get())
        }
        withCompactedStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 3", value.get())
        }
    }

    @Test
    fun `can update value in multiple sessions`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("value 1")
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())

            value.set("value 2")
            assertEquals("value 2", value.get())

            assertEquals(3, store.changes())
            assertEquals(3, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }
        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.changes())

            val value = store.value<String>("value")
            assertEquals("value 2", value.get())

            value.set("value 3")
            assertEquals("value 3", value.get())

            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 3", value.get())
        }
        withCompactedStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 3", value.get())
        }
    }

    @Test
    fun `compacts store after updating value many times in same session`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("initial value")
        }

        withMaxChanges(5) { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(2, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")

            for (i in 1..3) {
                value.set("value $i")
                assertEquals(i + 2, store.changes())
                assertEquals(store.changes(), store.changesSinceCompaction())
            }

            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.changes())
            assertEquals(5, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            value.set("value 4")

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            for (i in 5..9) {
                value.set("value $i")
                assertEquals(i - 2, store.changes())
                assertEquals(i - 4, store.changesSinceCompaction())
                assertEquals(2, store.generation())
            }

            assertEquals(listOf("value"), store.values())
            assertEquals(7, store.changes())
            assertEquals(5, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            for (i in 10..11) {
                value.set("value $i")
                assertEquals(i - 8, store.changes())
                assertEquals(i - 10, store.changesSinceCompaction())
                assertEquals(3, store.generation())
            }
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.changes())
            assertEquals(1, store.changesSinceCompaction())
            assertEquals(3, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 11", value.get())
        }
    }

    @Test
    fun `can update value many times in different sessions`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("value 1")
            value.set("value 2")
            value.set("value 3")
            value.set("value 4")
        }

        withMaxChanges(5) { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(5, store.changes())
            assertEquals(1, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 4", value.get())
            value.set("value 5")
            assertEquals("value 5", value.get())

            assertEquals(2, store.changes())
            assertEquals(2, store.generation())
        }

        withMaxChanges(5) { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.changes())
            assertEquals(2, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 5", value.get())
            value.set("value 6")
            assertEquals("value 6", value.get())

            assertEquals(3, store.changes())
            assertEquals(2, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(3, store.changes())
            assertEquals(2, store.generation())

            val value = store.value<String>("value")
            assertEquals("value 6", value.get())
        }
    }

    @Test
    fun `can read and write multiple values in same session`() {
        withStore { store ->
            val value1 = store.value<String>("value 1")
            assertNull(value1.get())
            val value2 = store.value<String>("value 2")
            assertNull(value2.get())

            value1.set("value 1")
            assertEquals("value 1", value1.get())

            value2.set("value 2")
            assertEquals("value 2", value2.get())

            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value1 = store.value<String>("value 1")
            val value2 = store.value<String>("value 2")
            assertEquals("value 1", value1.get())
            assertEquals("value 2", value2.get())
        }
        withCompactedStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value1 = store.value<String>("value 1")
            val value2 = store.value<String>("value 2")
            assertEquals("value 1", value1.get())
            assertEquals("value 2", value2.get())
        }
    }

    @Test
    fun `can read and write multiple values in different sessions`() {
        withStore { store ->
            val value1 = store.value<String>("value 1")
            assertNull(value1.get())

            value1.set("value 1")
            assertEquals("value 1", value1.get())

            assertEquals(listOf("value 1"), store.values())
            assertEquals(2, store.changes())
            assertEquals(2, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            val value1 = store.value<String>("value 1")
            assertEquals("value 1", value1.get())
            assertEquals(listOf("value 1"), store.values())
            assertEquals(2, store.changes())

            val value2 = store.value<String>("value 2")
            assertNull(value2.get())

            value2.set("value 2")
            assertEquals("value 2", value2.get())

            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            val value1 = store.value<String>("value 1")
            val value2 = store.value<String>("value 2")
            assertEquals("value 1", value1.get())
            assertEquals("value 2", value2.get())
        }
        withCompactedStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value1 = store.value<String>("value 1")
            val value2 = store.value<String>("value 2")
            assertEquals("value 1", value1.get())
            assertEquals("value 2", value2.get())
        }
    }

    @Test
    fun `can write multiple values after compacting discarded values`() {
        withStore { store ->
            val value1 = store.value<String>("value 1")
            value1.set("value 1")

            val value2 = store.value<String>("value 2")
            value2.set("value 1")

            val value3 = store.value<String>("value 3")
            value3.set("value 1")

            value1.discard()
            value3.discard()
            value2.discard()

            assertTrue(store.values().isEmpty())
            assertEquals(9, store.changes())
            assertEquals(9, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withCompactedStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(0, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())

            val value1 = store.value<String>("value 1")
            val value2 = store.value<String>("value 2")
            val value3 = store.value<String>("value 3")
            assertNull(value1.get())
            assertNull(value2.get())
            assertNull(value3.get())
        }

        withStore { store ->
            val value1 = store.value<String>("value 1")
            value1.set("new value 1")
            val value2 = store.value<String>("value 2")
            value2.set("new value 2")

            assertEquals("new value 1", value1.get())
            assertEquals("new value 2", value2.get())

            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }

        withStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())
            assertEquals(4, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }
        withCompactedStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
            assertEquals(4, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(3, store.generation())
        }
    }
}