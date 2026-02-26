package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.oneExcept
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class ZeroOrMoreOfOneExceptTest : AbstractParseTest() {
    @Test
    fun `matches zero or more of one except multi-byte literal`() {
        val parser = zeroOrMore(oneExcept(literal(byteArrayOf(0x1, 0x2))))

        parser.expecting {
            expectZeroOrMore {
                expectSequence {
                    expectNot {
                        expectLiteral(0x1, 0x2)
                    }
                    expectOneByte()
                }
            }
        }

        parser.matches(expected = emptyList())
        parser.matches(0x3, expected = bytes(0x3))
        parser.matches(0x3, 0x4, 0x5, expected = bytes(0x3, 0x4, 0x5))

        parser.matches(0x1, expected = bytes(0x1))
        parser.matches(0x1, 0x3, expected = bytes(0x1, 0x3))
        parser.matches(0x3, 0x1, 0x3, 0x2, expected = bytes(0x3, 0x1, 0x3, 0x2))

        parser.doesNotMatch(0x1, 0x2) {
            expect("any byte")
            expectEndOfInput()
            expect("not x01")
        }
        parser.doesNotMatch(0x3, 0x4, 0x1, 0x2) {
            failAt(2)
            expect("any byte")
            expectEndOfInput()
            expect("not x01")
        }
    }
}