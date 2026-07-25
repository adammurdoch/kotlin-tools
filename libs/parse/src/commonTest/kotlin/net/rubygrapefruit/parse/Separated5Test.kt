package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.separated
import kotlin.test.Test

class Separated5Test : AbstractParseTest() {
    @Test
    fun `matches literal then separator then literal then separator then literal`() {
        val parser = separated(
            literal(byteArrayOf(0x1), 1),
            literal(byteArrayOf(0x2), 2),
            literal(byteArrayOf(0x3), 3),
            literal(byteArrayOf(0x4), 4),
            literal(byteArrayOf(0x5), 5)
        ) { a, b, c -> listOf(a, b, c) }

        parser.expecting {
            expectSequence {
                expectLiteral(0x1, result = 1)
                expectSequence {
                    expectLiteral(0x2)
                    expectSequence {
                        expectLiteral(0x3, result = 3)
                        expectSequence {
                            expectLiteral(0x4)
                            expectLiteral(0x5, result = 5)
                        }
                    }
                }
            }
        }

        parser.matches(0x1, 0x2, 0x3, 0x4, 0x5, expected = listOf(1, 3, 5))

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
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(3)
            expectLiteral(0x4)
        }
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x4) {
            failAt(4)
            expectLiteral(0x5)
        }

        // unexpected
        parser.doesNotMatch(0x4) {
            expectLiteral(0x1)
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x4, 0x5, 0x6) {
            failAt(5)
            expectEndOfInput()
        }
    }
}