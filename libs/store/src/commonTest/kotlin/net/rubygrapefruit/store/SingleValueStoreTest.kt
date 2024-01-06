@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.*

class SingleValueStoreTest {
    private val fixture = FilesFixture()
    private val dir = fixture.dir("store")
    private val store = Store.open(dir)

    @AfterTest
    fun cleanup() {
        store.close()
    }

    @Test
    fun `can read from empty store`() {
        assertTrue(store.values().isEmpty())

        val value = store.value<String>("value")
        assertNull(value.get())

        assertEquals(listOf("value"), store.values())
    }

    @Test
    fun `can read from then write to store`() {
        val value = store.value<String>("value")
        assertNull(value.get())

        value.set("value 1")
        assertEquals("value 1", value.get())

        assertEquals(listOf("value"), store.values())
    }

    @Test
    fun `can write to then read from store`() {
        val value = store.value<String>("value")

        value.set("value 1")
        assertEquals("value 1", value.get())

        assertEquals(listOf("value"), store.values())
    }

    @Test
    fun `can read and write multiple values to store`() {
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

    @Test
    fun `value is persistent`() {
        val value = store.value<String>("value")

        value.set("value 1")
        store.close()

        Store.open(dir).use {
            assertEquals(listOf("value"), store.values())

            val value2 = it.value<String>("value")
            assertEquals("value 1", value2.get())
        }
    }

    @Test
    fun `can discard value`() {
        val value = store.value<String>("empty")
        value.set("value 1")
        assertEquals("value 1", value.get())

        value.discard()
        assertNull(value.get())

        assertTrue(store.values().isEmpty())
    }

    private fun Store.values(): List<String> {
        val result = mutableListOf<String>()
        accept(object : ContentVisitor {
            override fun value(name: String, details: ContentVisitor.ValueInfo) {
                result.add(name)
            }
        })
        return result
    }
}