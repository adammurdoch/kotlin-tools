package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class Sequence3Test : AbstractParseTest() {
    @Test
    fun `matches sequence of byte literals`() {
        val parser = sequence(
            literal(byteArrayOf(0x1), 1),
            literal(byteArrayOf(0x2), 2),
            literal(byteArrayOf(0x3), 3)
        ) { a, b, c -> listOf(a, b, c) }

        parser.expecting {
            expectSequence {
                expectLiteral(0x1, result = 1)
                expectSequence {
                    expectLiteral(0x2, result = 2)
                    expectLiteral(0x3, result = 3)
                }
            }
        }

        parser.matches(0x1, 0x2, 0x3, expected = listOf(0x1, 0x2, 0x3))

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

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x4) {
            failAt(3)
            expectEndOfInput()
        }
    }
}