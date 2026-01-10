package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.literal
import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class SequenceTest : AbstractParseTest() {
    @Test
    fun `matches single char literals`() {
        val parser = sequence(literal("a", 1), literal("b", 2)) { a, b -> listOf(a, b) }

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

        parser.matches(0x1, 0x2, 0x3, 0x4, expected = listOf(1, 2))

        // missing
        parser.doesNotMatch {
            expect("x01")
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expect("x02")
        }
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(3)
            expect("x04")
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expect("x01")
        }
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x1) {
            failAt(3)
            expect("x04")
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x4, 0x5) {
            failAt(4)
            expectEndOfInput()
        }
    }
}