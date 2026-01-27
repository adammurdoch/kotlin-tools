package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.replace
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ReplaceTest : AbstractParseTest() {
    @Test
    fun `replaces value of char literal`() {
        val parser = replace(literal("ab", 1), 2)

        parser.expecting {
            expectMap {
                expectLiteral("ab")
            }
        }

        parser.matches("ab", expected = 2)

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("ab")
        }

        // extra
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `replaces value of char literal with no value`() {
        val parser = replace(literal("ab"), 2)

        parser.expecting {
            expectMap {
                expectLiteral("ab")
            }
        }

        parser.matches("ab", expected = 2)

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("ab")
        }

        // extra
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
    }
}