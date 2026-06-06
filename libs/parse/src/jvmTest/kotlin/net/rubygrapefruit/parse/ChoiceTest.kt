package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ChoiceTest : AbstractParseTest() {
    @Test
    fun `matches char literals with no common prefix`() {
        val parser = oneOf(
            literal("abcd", 1),
            literal("12", 2)
        )

        parser.expecting {
            expectChoice {
                expectLiteral("12", result = 1)
                expectLiteral("abcd", result = 2)
            }
        }

        parser.matches("abcd", expected = 1) {
            steps {
                advance(1)
                advance(3)
            }
        }
        parser.matches("12", expected = 2) {
            steps {
                advance(1)
                advance(1)
            }
        }

        // missing
        parser.doesNotMatch("") {
            expectLiteral("12")
            expectLiteral("abcd")
            steps { }
        }

        // partial match one
        parser.doesNotMatch("ab") {
            expectLiteral("12")
            expectLiteral("abcd")
            steps {
                advance(1)
            }
        }
        parser.doesNotMatch("1") {
            expectLiteral("12")
            expectLiteral("abcd")
            steps {
            }
        }

        // extra
        parser.doesNotMatch("abcdX") {
            failAt(4)
            expectEndOfInput()
            steps {
                advance(1)
                advance(3)
            }
        }
        parser.doesNotMatch("12X") {
            failAt(2)
            expectEndOfInput()
            steps {
                advance(1)
                advance(1)
            }
        }
    }

    @Test
    fun `matches byte literals with no common prefix`() {
        val parser = oneOf(
            literal(byteArrayOf(0x1, 0x2, 0x3, 0x4), 1),
            literal(byteArrayOf(0x10, 0x11), 2)
        )

        parser.expecting {
            expectChoice {
                expectLiteral(0x1, 0x2, 0x3, 0x4, result = 1)
                expectLiteral(0x10, 0x11, result = 2)
            }
        }

        parser.matches(0x1, 0x2, 0x3, 0x4, expected = 1) {
            steps {
                advance(1)
                advance(3)
            }
        }
        parser.matches(0x10, 0x11, expected = 2) {
            steps {
                advance(1)
                advance(1)
            }
        }

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            expectLiteral(0x10)
            steps { }
        }

        // partial match one
        parser.doesNotMatch(0x1, 0x2) {
            failAt(2)
            expectLiteral(0x3)
            steps {
                advance(1)
                // byte literal does not look ahead for end of input
                advance(1)
            }
        }
        parser.doesNotMatch(0x10) {
            failAt(1)
            expectLiteral(0x11)
            steps {
                advance(1)
            }
        }
    }

    @Test
    fun `matches literals with common prefix`() {
        val parser = oneOf(
            literal("a---", 1),
            literal("a...", 2)
        )

        parser.expecting {
            expectChoice {
                expectLiteral("a---", result = 1)
                expectLiteral("a...", result = 2)
            }
        }

        parser.matches("a---", expected = 1) {
            steps {
                advance(1)
                advance(1)
                advance(2) // discards failed branch
            }
        }
        parser.matches("a...", expected = 2) {
            steps {
                advance(1)
                advance(1)
                advance(2) // discards failed branch
            }
        }

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("a---")
            expectLiteral("a...")
            steps {
                // char literal looks ahead for end of input
            }
        }

        // unexpected
        parser.doesNotMatch("a..X") {
            expectLiteral("a---")
            expectLiteral("a...")
            steps {
                advance(1)
                advance(1)
            }
        }

        // extra
        parser.doesNotMatch("a...X") {
            failAt(4)
            expectEndOfInput()
            steps {
                advance(1)
                advance(1)
                advance(2)
            }
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

        parser.matches("abc", expected = 1) {
            steps {
                advance(1)
                advance(1)
                advance(1)
            }
        }
        parser.matches("ab", expected = 2) {
            steps {
                advance(1)
                advance(1)
            }
        }

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
            expectLiteral("abc")
            steps { }
        }
        parser.doesNotMatch("a") {
            expectLiteral("ab")
            expectLiteral("abc")
            steps {
                // char literal looks ahead for end of input
            }
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
            steps {
                advance(1)
                advance(1)
                advance(1)
            }
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
            steps {
                advance(1)
                advance(1)
            }
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

        parser.matches("abc", expected = 1) {
            steps {
                advance(1)
                advance(1)
                advance(1)
            }
        }
        parser.matches("ad", expected = 2) {
            steps {
                advance(1)
                advance(1)
            }
        }
        parser.matches("ab", expected = 3) {
            steps {
                advance(1)
                advance(1)
            }
        }

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
            expectLiteral("abc")
            expectLiteral("ad")
            steps { }
        }
        parser.doesNotMatch("a") {
            expectLiteral("ab")
            expectLiteral("abc")
            expectLiteral("ad")
            steps { }
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