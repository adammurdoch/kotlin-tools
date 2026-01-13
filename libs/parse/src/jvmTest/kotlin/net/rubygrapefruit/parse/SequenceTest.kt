package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.literal
import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class SequenceTest : AbstractParseTest() {
    @Test
    fun `matches single char literals`() {
        val parser = sequence(literal("a", 1), literal("b", 2)) { a, b -> listOf(a, b) }

        parser.expecting {
            expectLiteral("a")
        }

        parser.matches("ab", expected = listOf(1, 2))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
        }
        parser.doesNotMatch("aX") {
            failAt(1)
            expectLiteral("b")
        }

        // extra
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches multi-byte literals`() {
        val parser = sequence(literal(byteArrayOf(0x1, 0x2), 1), literal(byteArrayOf(0x3, 0x4), 2)) { a, b -> listOf(a, b) }

        parser.expecting {
            expectLiteral(0x1)
        }

        parser.matches(0x1, 0x2, 0x3, 0x4, expected = listOf(1, 2))

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(3)
            expectLiteral(0x4)
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expectLiteral(0x1)
        }
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x1) {
            failAt(3)
            expectLiteral(0x4)
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x4, 0x5) {
            failAt(4)
            expectEndOfInput()
        }
    }
}