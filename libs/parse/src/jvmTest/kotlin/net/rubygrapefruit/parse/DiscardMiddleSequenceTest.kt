package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class DiscardMiddleSequenceTest : AbstractParseTest() {
    @Test
    fun `matches literal then literal then literal`() {
        val parser = sequence(
            literal(byteArrayOf(0x1), 1),
            literal(byteArrayOf(0x2)),
            literal(byteArrayOf(0x3), 3)
        ) { a, b -> listOf(a, b) }

        parser.expecting {
            expectSequence {
                expectLiteral(0x1, result = 1)
                expectSequence {
                    expectLiteral(0x2)
                    expectLiteral(0x3, result = 3)
                }
            }
        }

        parser.matches(0x1, 0x2, 0x3, expected = listOf(1, 3))

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x1, 0x2) {
            failAt(2)
            expectLiteral(0x3)
        }

        // unexpected
        parser.doesNotMatch(0x4) {
            expectLiteral(0x1)
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x4) {
            failAt(3)
            expectEndOfInput()
        }
    }
}