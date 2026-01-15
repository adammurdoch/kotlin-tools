package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.combinators.oneOf
import kotlin.test.Test

class ChoiceTest : AbstractParseTest() {
    @Test
    fun `matches char literals with no common prefix`() {
        val parser = oneOf(literal("abc", 1), literal("12", 2))

        parser.expecting {
            expectLiteral("12")
            expectLiteral("abc")
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
        val parser = oneOf(literal(byteArrayOf(0x1, 0x2, 0x3), 1), literal(byteArrayOf(0x10, 0x11), 2))

        parser.expecting {
            expectLiteral(0x1)
            expectLiteral(0x10)
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
        val parser = oneOf(literal("abc", 1), literal("abd", 2))

        parser.expecting {
            expectLiteral("abc")
            expectLiteral("abd")
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
        val parser = oneOf(literal("abc", 1), literal("ab", 2))

        parser.expecting {
            expectLiteral("ab")
            expectLiteral("abc")
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
    fun `uses result from first parser that matches`() {
        val parser = oneOf(literal("ab", 1), literal("abc", 2))

        parser.matches("ab", expected = 1)

        parser.doesNotMatch("abc") {
            failAt(2)
            expectEndOfInput()
        }
    }
}