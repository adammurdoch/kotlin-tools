package net.rubygrapefruit.store

import kotlinx.io.buffered
import net.rubygrapefruit.file.regularFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class StoreTest : AbstractStoreTest() {
    @Test
    fun `can open empty store`() {
        withStore { store ->
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
        }
    }

    @Test
    fun `cannot open when store file has incorrect version`() {
        withStore {
            it.value<String>("value").set("ok")
        }

        val storeFile = testStore.file(StoreFile.Metadata.name)
        assertTrue(storeFile.metadata().regularFile)

        storeFile.withContent {
            // Overwrite the first byte
            val bufferedSink = it.sink.buffered()
            bufferedSink.write(byteArrayOf(1))
            bufferedSink.flush()
        }

        try {
            Store.open(testStore)
            fail()
        } catch (e: IllegalStateException) {
            val message = e.message
            assertTrue(message != null && message.startsWith("Unexpected store version in file ${storeFile.absolutePath}."), "message: '$message'")
        }

        withDiscardedStore()
    }

    @Test
    fun `can discard store content on open`() {
        withStore { store ->
            store.value<Long>("long").set(123)
            store.map<Long, String>("longs").set(123, "value")
        }

        withDiscardedStore { store ->
            assertTrue(store.values().isEmpty())
        }

        withStore { store ->
            assertTrue(store.values().isEmpty())
        }
    }

    @Test
    fun `can compact store content on open`() {
        withStore { store ->
            val value = store.value<Int>("int")
            val map = store.map<Int, String>("ints")
            for (i in 1..10) {
                value.set(i)
                map.set(i, i.toString())
            }

            assertEquals(22, store.changes())
            assertEquals(22, store.changesSinceCompaction())
            assertEquals(1, store.generation())
        }

        withCompactedStore { store ->
            assertEquals(13, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }

        withCompactedStore { store ->
            // Does not compact again
            assertEquals(13, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }
    }

    @Test
    fun `compacts store after writing many values`() {
        withStore { store ->
            for (i in 1..10) {
                store.value<Long>("long $i").set(123)
            }
        }

        withMaxChanges(5) { store ->
            assertEquals(10, store.values().size)
            assertEquals(20, store.changes())
            assertEquals(20, store.changesSinceCompaction())
            assertEquals(1, store.generation())

            store.value<String>("string").set("value")

            assertEquals(11, store.values().size)
            assertEquals(22, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }

        withStore { store ->
            assertEquals(11, store.values().size)
            assertEquals(22, store.changes())
            assertEquals(0, store.changesSinceCompaction())
            assertEquals(2, store.generation())
        }
    }

    @Test
    fun `can create many values without writing value`() {
        withMaxChanges(5) { store ->
            for (i in 1..10) {
                store.value<Long>("long $i")
            }

            assertEquals(0, store.values().size)
            assertEquals(0, store.changes())
            assertEquals(1, store.generation())
        }

        withStore { store ->
            assertEquals(0, store.values().size)
            assertEquals(0, store.changes())
            assertEquals(1, store.generation())
        }
    }

    @Test
    fun `cannot open key value store as a value store`() {
        withStore { store ->
            store.map<String, String>("value").set("a", "value 1")

            try {
                store.value<String>("value")
                fail()
            } catch (e: IllegalArgumentException) {
                assertEquals("Cannot open stored map 'value' as a stored value.", e.message)
            }
        }
        withStore { store ->
            try {
                store.value<String>("value")
                fail()
            } catch (e: IllegalArgumentException) {
                assertEquals("Cannot open stored map 'value' as a stored value.", e.message)
            }
        }
    }

    @Test
    fun `cannot open value store as a key value store`() {
        withStore { store ->
            store.value<String>("value").set("value 1")

            try {
                store.map<String, String>("value")
                fail()
            } catch (e: IllegalArgumentException) {
                assertEquals("Cannot open stored value 'value' as a stored map.", e.message)
            }
        }
        withStore { store ->
            try {
                store.map<String, String>("value")
                fail()
            } catch (e: IllegalArgumentException) {
                assertEquals("Cannot open stored value 'value' as a stored map.", e.message)
            }
        }
    }
}