package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.optional
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class OptionalTest : AbstractParseTest() {
    @Test
    fun `parses optional char literal`() {
        val parser = optional(literal("abc", 1))

        parser.expecting {
            expectChoice {
                expectLiteral("abc", result = 1)
                expectSucceed(result = null)
            }
        }

        parser.matches("abc", expected = 1) {
            steps {
                advance(0) // missing branch succeeds
                advance(1)
                advance(1)
                advance(1, commit = 3)
            }
        }
        parser.matches("", expected = null) {
            steps {
                advance(0)
            }
        }

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectEndOfInput()
            steps {
                advance(0)
            }
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
            steps {
                advance(0)
                advance(1)
                advance(1)
                advance(1, commit = 3)
            }
        }
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectEndOfInput()
            steps {
                advance(0)
            }
        }
    }

    @Test
    fun `parses optional byte literal`() {
        val parser = optional(literal(byteArrayOf(0x1, 0x2, 0x3), 1))

        parser.expecting {
            expectChoice {
                expectLiteral(0x1, 0x2, 0x3, result = 1)
                expectSucceed(result = null)
            }
        }

        parser.matches(0x1, 0x2, 0x3, expected = 1) {
            steps {
                advance(0) // missing branch succeeds
                commit(1)
                commit(2)
            }
        }
        parser.matches(expected = null) {
            steps {
                advance(0)
            }
        }
    }

    @Test
    fun `produces value when match not present`() {
        val parser = optional(literal("ab", 1), 0)

        parser.matches("ab", expected = 1)
        parser.matches("", expected = 0)
    }
}