package net.rubygrapefruit.plugins.app.internal

import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleExtensionsTest {
    @Test
    fun testExtensions() {
        assertEquals("aB", toModuleName("a-b"))
        assertEquals("aB", toModuleName("a b"))
        assertEquals("SomeApp", toModuleName("Some App"))
    }
}