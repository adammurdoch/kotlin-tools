package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ChoiceTest : AbstractParseTest() {
    @Test
    fun `matches char literals with no common prefix`() {
        val parser = oneOf(
            literal("abc", 1),
            literal("12", 2)
        )

        parser.expecting {
            expectChoice {
                expectLiteral("12", result = 1)
                expectLiteral("abc", result = 2)
            }
        }

        parser.matches("abc", expected = 1)
        parser.matches("12", expected = 2)

        // missing
        parser.doesNotMatch("") {
            expectLiteral("12")
            expectLiteral("abc")
        }

        // partial match one
        parser.doesNotMatch("ab") {
            expectLiteral("12")
            expectLiteral("abc")
        }
        parser.doesNotMatch("1") {
            expectLiteral("12")
            expectLiteral("abc")
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
        parser.doesNotMatch("12X") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches byte literals with no common prefix`() {
        val parser = oneOf(
            literal(byteArrayOf(0x1, 0x2, 0x3), 1),
            literal(byteArrayOf(0x10, 0x11), 2)
        )

        parser.expecting {
            expectChoice {
                expectLiteral(0x1, 0x2, 0x3, result = 1)
                expectLiteral(0x10, 0x11, result = 2)
            }
        }

        parser.matches(0x1, 0x2, 0x3, expected = 1)
        parser.matches(0x10, 0x11, expected = 2)

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            expectLiteral(0x10)
        }

        // partial match one
        parser.doesNotMatch(0x1, 0x2) {
            failAt(2)
            expectLiteral(0x3)
        }
        parser.doesNotMatch(0x10) {
            failAt(1)
            expectLiteral(0x11)
        }
    }

    @Test
    fun `matches literals with common prefix`() {
        val parser = oneOf(
            literal("abc", 1),
            literal("abd", 2)
        )

        parser.expecting {
            expectChoice {
                expectLiteral("abc", result = 1)
                expectLiteral("abd", result = 2)
            }
        }

        parser.matches("abc", expected = 1)
        parser.matches("abd", expected = 2)

        parser.doesNotMatch("ab") {
            expectLiteral("abc")
            expectLiteral("abd")
        }
    }

    @Test
    fun `matches literals where one literal is a prefix of another`() {
        val parser = oneOf(
            literal("abc", 1),
            literal("ab", 2)
        )

        parser.expecting {
            expectChoice {
                expectLiteral("ab", result = 1)
                expectLiteral("abc", result = 2)
            }
        }

        parser.matches("abc", expected = 1)
        parser.matches("ab", expected = 2)

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
            expectLiteral("abc")
        }
        parser.doesNotMatch("a") {
            expectLiteral("ab")
            expectLiteral("abc")
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches literals with common prefix and where one literal is a prefix of another`() {
        val parser = oneOf(
            literal("abc", 1),
            literal("ad", 2),
            literal("ab", 3)
        )

        parser.expecting {
            expectChoice {
                expectLiteral("ab", result = 1)
                expectLiteral("abc", result = 2)
                expectLiteral("ad", result = 3)
            }
        }

        parser.matches("abc", expected = 1)
        parser.matches("ad", expected = 2)
        parser.matches("ab", expected = 3)

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("a") {
            expectLiteral("ab")
            expectLiteral("abc")
            expectLiteral("ad")
        }
    }

    @Test
    fun `uses result from first parser that matches`() {
        val parser = oneOf(
            literal("ab", 1),
            literal("abc", 2)
        )

        parser.matches("ab", expected = 1)

        parser.doesNotMatch("abc") {
            failAt(2)
            expectEndOfInput()
        }
    }
}