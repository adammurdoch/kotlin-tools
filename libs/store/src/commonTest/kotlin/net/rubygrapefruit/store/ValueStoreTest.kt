package net.rubygrapefruit.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValueStoreTest : AbstractStoreTest() {
    @Test
    fun `can read from empty store`() {
        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(0, store.indexUpdates())

            val value = store.value<String>("value")
            assertNull(value.get())

            // Not visible until it has been written to
            assertTrue(store.values().isEmpty())
            assertEquals(1, store.indexUpdates())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
            assertEquals(1, store.indexUpdates())

            val value = store.value<String>("value")
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
            assertEquals(1, store.indexUpdates())
        }
    }

    @Test
    fun `can read then write`() {
        withStore { store ->
            val value = store.value<String>("value")
            assertNull(value.get())

            value.set("value 1")
            assertEquals("value 1", value.get())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.indexUpdates())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.indexUpdates())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())

            assertEquals(listOf("value"), store.values())
            assertEquals(2, store.indexUpdates())
        }
    }

    @Test
    fun `can write then read`() {
        withStore { store ->
            val value = store.value<String>("value")

            value.set("value 1")
            assertEquals("value 1", value.get())

            assertEquals(listOf("value"), store.values())
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())
            val value = store.value<String>("value")
            assertEquals("value 1", value.get())
        }
    }

    @Test
    fun `can discard value`() {
        withStore { store ->
            val value = store.value<String>("empty")
            value.set("value 1")
            assertEquals("value 1", value.get())

            value.discard()
            assertNull(value.get())

            assertTrue(store.values().isEmpty())
        }
        withStore { store ->
            assertTrue(store.values().isEmpty())
            val value = store.value<String>("empty")
            assertNull(value.get())
            assertTrue(store.values().isEmpty())
        }
    }

    @Test
    fun `can discard value then write in same session`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("value 1")
            assertEquals("value 1", value.get())

            value.discard()
            assertNull(value.get())
            assertTrue(store.values().isEmpty())

            value.set("value 2")

            assertEquals("value 2", value.get())
            assertEquals(listOf("value"), store.values())
        }
        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.value<String>("value")
            assertEquals("value 2", value.get())
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
            assertEquals(listOf("value"), store.values())

            value.discard()
            assertNull(value.get())
            assertTrue(store.values().isEmpty())
        }
        withStore { store ->
            val value = store.value<String>("value")
            assertNull(value.get())
            assertTrue(store.values().isEmpty())

            value.set("value 2")

            assertEquals("value 2", value.get())
            assertEquals(listOf("value"), store.values())
        }
        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.value<String>("value")
            assertEquals("value 2", value.get())
        }
    }

    @Test
    fun `can update`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("value 1")
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())
            value.set("value 2")
            assertEquals("value 2", value.get())
        }
        withStore { store ->
            assertEquals(listOf("value"), store.values())
            val value = store.value<String>("value")
            assertEquals("value 2", value.get())
        }
    }

    @Test
    fun `can update multiple times in same session`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("value 1")
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())
            value.set("value 2")
            value.set("value 3")
            assertEquals("value 3", value.get())
        }
        withStore { store ->
            assertEquals(listOf("value"), store.values())
            val value = store.value<String>("value")
            assertEquals("value 3", value.get())
        }
    }

    @Test
    fun `can update multiple times in different sessions`() {
        withStore { store ->
            val value = store.value<String>("value")
            value.set("value 1")
        }

        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.value<String>("value")
            assertEquals("value 1", value.get())

            value.set("value 2")
            assertEquals("value 2", value.get())
        }
        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.value<String>("value")
            assertEquals("value 2", value.get())

            value.set("value 3")
            assertEquals("value 3", value.get())
        }
        withStore { store ->
            assertEquals(listOf("value"), store.values())

            val value = store.value<String>("value")
            assertEquals("value 3", value.get())
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
        }

        withStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
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
        }

        withStore { store ->
            val value1 = store.value<String>("value 1")
            assertEquals("value 1", value1.get())
            assertEquals(listOf("value 1"), store.values())

            val value2 = store.value<String>("value 2")
            assertNull(value2.get())

            value2.set("value 2")
            assertEquals("value 2", value2.get())

            assertEquals(listOf("value 1", "value 2"), store.values())
        }

        withStore { store ->
            assertEquals(listOf("value 1", "value 2"), store.values())
            val value1 = store.value<String>("value 1")
            val value2 = store.value<String>("value 2")
            assertEquals("value 1", value1.get())
            assertEquals("value 2", value2.get())
        }
    }
}