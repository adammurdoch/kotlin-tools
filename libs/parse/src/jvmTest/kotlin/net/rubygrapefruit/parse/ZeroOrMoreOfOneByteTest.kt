package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.binary.one
import kotlin.test.Test

class ZeroOrMoreOfOneByteTest : AbstractParseTest() {
    /**
     * NOTE: this is in a separate class to the text equivalent because importing both `one()` overloads in the same class is painful.
     */
    @Test
    fun `matches zero or more of one byte`() {
        val parser = zeroOrMore(
            one()
        )

        parser.expecting {
            expectZeroOrMoreSingleInput {
                expectOneByte()
            }
        }

        parser.matches(expected = emptyList()) {
            steps {
                advance(0)
            }
        }
        parser.matches(0x1, expected = bytes(0x1)) {
            steps {
                advance(1)
            }
        }
        parser.matches(0x5, expected = bytes(0x5))
        parser.matches(0x5, 0x1, 0x1, expected = bytes(0x5, 0x1, 0x1)) {
            steps {
                advance(3)
            }
        }
    }
}