package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.general.succeed
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class SequenceOfSucceedTest : AbstractParseTest() {
    @Test
    fun `matches succeed then succeed`() {
        val parser = sequence(succeed(1), succeed(2)) { a, b -> listOf(a, b) }

        parser.expecting {
            emptyMatch()
            expectSequence {
                expectSucceed(result = 1)
                expectSucceed(result = 2)
            }
        }

        parser.matches("", expected = listOf(1, 2))

        // extra
        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
    }

    @Test
    fun `matches succeed then literal`() {
        val parser = sequence(succeed(1), literal("ab", 2)) { a, b -> listOf(a, b) }

        parser.expecting {
            expectSequence {
                expectSucceed(result = 1)
                expectLiteral("ab", result = 2)
            }
        }

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
        val parser = sequence(literal("ab", 1), succeed(2)) { a, b -> listOf(a, b) }

        parser.expecting {
            expectSequence {
                expectLiteral("ab", result = 1)
                expectSucceed(result = 2)
            }
        }

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