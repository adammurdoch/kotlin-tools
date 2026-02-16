package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.optional
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class OptionalTest : AbstractParseTest() {
    @Test
    fun `parses optional char literal`() {
        val parser = optional(literal("abc", 1))

        parser.matches("abc", expected = 1)
        parser.matches("", expected = null)

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectEndOfInput()
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectEndOfInput()
        }
    }

    @Test
    fun `produces value when match not present`() {
        val parser = optional(literal("ab", 1), 0)

        parser.matches("ab", expected = 1)
        parser.matches("", expected = 0)
    }
}