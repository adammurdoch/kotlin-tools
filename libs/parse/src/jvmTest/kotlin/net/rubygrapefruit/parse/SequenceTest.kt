package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.literal
import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class SequenceTest : AbstractParseTest() {
    @Test
    fun `matches char literals`() {
        val parser = sequence(literal("a", 1), literal("b", 2)) { a, b -> a + b }

        parser.matches("ab", expected = 3)

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
    fun `matches byte literals`() {
        val parser = sequence(literal(byteArrayOf(0x1), 1), literal(byteArrayOf(0x2), 2)) { a, b -> a + b }

        parser.matches(0x1, 0x2, expected = 3)
    }
}