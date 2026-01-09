package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.literal
import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.oneOf
import kotlin.test.Test

class ChoiceTest : AbstractParseTest() {
    @Test
    fun `matches char literals with no common prefix`() {
        val parser = oneOf(literal("abc", 1), literal("12", 2))

        parser.matches("abc", expected = 1)
        parser.matches("12", expected = 2)

        // missing
        parser.doesNotMatch("") {
            expectLiteral("abc")
            expectLiteral("12")
        }

        // partial match one
        parser.doesNotMatch("ab") {
            expectLiteral("abc")
            expectLiteral("12")
        }
        parser.doesNotMatch("1") {
            expectLiteral("abc")
            expectLiteral("12")
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

        parser.matches(0x1, 0x2, 0x3, expected = 1)
        parser.matches(0x10, 0x11, expected = 2)

        // missing
        parser.doesNotMatch {
            expect("x01")
            expect("x10")
        }

        // partial match one
        parser.doesNotMatch(0x1, 0x2) {
            failAt(2)
            expect("x03")
        }
        parser.doesNotMatch(0x10) {
            failAt(1)
            expect("x11")
        }
    }

    @Test
    fun `matches literals with common prefix`() {
        val parser = oneOf(literal("abc", 1), literal("abd", 2))

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

        parser.matches("abc", expected = 1)
        parser.matches("ab", expected = 2)

        // missing
        parser.doesNotMatch("") {
            expectLiteral("abc")
            expectLiteral("ab")
        }
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectLiteral("ab")
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