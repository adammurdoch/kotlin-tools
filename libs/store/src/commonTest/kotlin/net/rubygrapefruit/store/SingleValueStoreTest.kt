@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import kotlinx.serialization.builtins.serializer
import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SingleValueStoreTest {
    private val fixture = FilesFixture()
    val dir = fixture.dir("store")
    private val store = Store.open(dir)

    @AfterTest
    fun cleanup() {
        store.close()
    }

    @Test
    fun `can read from empty store`() {
        val value = store.value("value", String.serializer())
        assertNull(value.get())
    }

    @Test
    fun `can read and write to store`() {
        val value = store.value("value", String.serializer())
        assertNull(value.get())

        value.set("value 1")
        assertEquals("value 1", value.get())
    }

    @Test
    fun `can read and write multiple values to store`() {
        val value1 = store.value("value 1", String.serializer())
        assertNull(value1.get())
        val value2 = store.value("value 2", String.serializer())
        assertNull(value2.get())

        value1.set("value 1")
        assertEquals("value 1", value1.get())

        value2.set("value 2")
        assertEquals("value 2", value2.get())
    }

    @Test
    fun `value is persistent`() {
        val value = store.value("value", String.serializer())

        value.set("value 1")
        store.close()

        Store.open(dir).use {
            val value2 = it.value("value", String.serializer())
            assertEquals("value 1", value2.get())
        }
    }

    @Test
    fun `can discard value`() {
        val value = store.value("empty", String.serializer())
        value.set("value 1")
        assertEquals("value 1", value.get())

        value.discard()
        assertNull(value.get())
    }
}