package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.consume
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.parse
import net.rubygrapefruit.parse.text.pushParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ConsumeTest : AbstractParseTest() {
    @Test
    fun `calls function with result of literal`() {
        var counter = 0
        val parser = consume(literal("abc", 1)) {
            counter++
        }

        parser.expecting {
            expectMap {
                expectLiteral("abc", result = 1)
            }
        }

        val result = parser.parse("abc")
        assertIs<ParseResult.Success<*>>(result)
        assertEquals(1, counter)
    }

    @Test
    fun `calls function when matching input becomes available`() {
        var counter = 0
        val parser = consume(literal("abc", 1)) {
            counter++
        }

        val pushParser = parser.pushParser()
        pushParser.input("ab".toCharArray())
        assertEquals(0, counter)

        pushParser.input("c".toCharArray())
        assertEquals(1, counter)

        val result = pushParser.endOfInput()
        assertIs<ParseResult.Success<*>>(result)
        assertEquals(1, counter)
    }
}