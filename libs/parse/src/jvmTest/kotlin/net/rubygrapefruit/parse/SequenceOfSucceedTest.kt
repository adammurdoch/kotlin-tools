package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class SequenceOfSucceedTest : AbstractParseTest() {
    @Test
    fun `matches succeed then succeed`() {
        val parser = sequence(succeed(1), succeed(2)) { a, b -> listOf(a,b) }

        parser.matches("", expected = listOf(1, 2))

        // extra
        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
    }

    @Test
    fun `matches succeed then literal`() {
        val parser = sequence(succeed(1), literal("ab", 2)) { a, b -> listOf(a,b) }

        parser.matches("ab", expected = listOf(1, 2))

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("ab")
        }

        // unexpected
        parser.doesNotMatch("aX") {
            expectLiteral("ab")
        }

        // extra
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches literal then succeed`() {
        val parser = sequence(literal("ab", 1), succeed(2)) { a, b -> listOf(a,b) }

        parser.matches("ab", expected = listOf(1, 2))

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("ab")
        }

        // unexpected
        parser.doesNotMatch("aX") {
            expectLiteral("ab")
        }

        // extra
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
    }
}