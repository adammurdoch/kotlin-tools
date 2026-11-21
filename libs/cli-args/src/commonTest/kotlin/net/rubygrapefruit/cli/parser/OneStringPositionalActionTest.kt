package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class OneStringPositionalActionTest : AbstractActionParseTest() {
    @Test
    fun `parses positional`() {
        var value: String? = null
        val parser = action(positional("a")) { a -> value = a }

        parser.parse("value").get().run {
            TestContext.run()
        }
        assertEquals("value", value)
    }
}