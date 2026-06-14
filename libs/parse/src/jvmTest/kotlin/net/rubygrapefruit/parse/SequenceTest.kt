package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.parse
import kotlin.test.Test

class SequenceTest : AbstractParseTest() {
    @Test
    fun `matches single text literals`() {
        val parser = sequence(literal("a", 1), literal("b", 2)) { a, b -> listOf(a, b) }

        parser.expecting {
            expectSequence {
                expectLiteral("a", result = 1)
                expectLiteral("b", result = 2)
            }
        }

        parser.matches("ab", expected = listOf(1, 2)) {
            steps {
                advance(1)
                advance(1)
            }
        }

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
            steps { }
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
            steps {
                advance(1)
            }
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
            steps { }
        }
        parser.doesNotMatch("aX") {
            failAt(1)
            expectLiteral("b")
            steps {
                advance(1)
            }
        }

        // extra
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
    fun `matches multi-byte literals`() {
        val parser = sequence(literal(byteArrayOf(0x1, 0x2), 1), literal(byteArrayOf(0x3, 0x4), 2)) { a, b -> listOf(a, b) }

        parser.expecting {
            expectSequence {
                expectLiteral(0x1, 0x2, result = 1)
                expectLiteral(0x3, 0x4, result = 2)
            }
        }

        parser.matches(0x1, 0x2, 0x3, 0x4, expected = listOf(1, 2)) {
            steps {
                advance(2)
                advance(2)
            }
        }

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            steps { }
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
            steps {
                advance(1)
            }
        }
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(3)
            expectLiteral(0x4)
            steps {
                advance(2)
                advance(1)
            }
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expectLiteral(0x1)
            steps { }
        }
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x1) {
            failAt(3)
            expectLiteral(0x4)
            steps {
                advance(2)
            }
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x4, 0x5) {
            failAt(4)
            expectEndOfInput()
            steps {
                advance(2)
                advance(2)
            }
        }
    }

    @Test
    fun `rethrows mapping failure`() {
        val failure = RuntimeException()
        val parser = sequence(literal("a", 1), literal("b", 2)) { _, _ ->
            failure.fillInStackTrace()
            throw failure
        }

        failsWith(failure) {
            parser.parse("ab")
        }
    }
}