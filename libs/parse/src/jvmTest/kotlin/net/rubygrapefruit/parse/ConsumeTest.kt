package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.consume
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.parse
import kotlin.test.Test
import kotlin.test.assertEquals

class ConsumeTest : AbstractParseTest() {
    @Test
    fun `calls function with result of literal`() {
        var result: Int? = 0
        val parser = consume(literal("abc", 1)) {
            result = it
        }

        parser.parse("abc")
        assertEquals(1, result)
    }
}