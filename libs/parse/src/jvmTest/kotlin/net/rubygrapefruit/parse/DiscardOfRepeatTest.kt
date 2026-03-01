package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.repeat
import kotlin.test.Test

class DiscardOfRepeatTest : AbstractParseTest() {
    @Test
    fun `discards result of n binary literals`() {
        val parser = discard(repeat(2, literal(byteArrayOf(0x1, 0x2), 4)))

        parser.expecting {
            expectRepeat(2, hasResult = false) {
                expectLiteral(0x1, 0x2, result = Unit)
            }
        }

        parser.matches(0x1, 0x2, 0x1, 0x2)

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
        }

        // unexpected
        parser.doesNotMatch(0x1, 0x2, 0, 0x2) {
            failAt(2)
            expectLiteral(0x1)
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x1, 0x2, 0) {
            failAt(4)
            expectEndOfInput()
        }
    }

    @Test
    fun `discards result of n binary literals with no result`() {
        val parser = discard(repeat(2, literal(byteArrayOf(0x1, 0x2))))

        parser.expecting {
            expectRepeat(2, hasResult = false) {
                expectLiteral(0x1, 0x2, result = Unit)
            }
        }

        parser.matches(0x1, 0x2, 0x1, 0x2)

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
        }

        // unexpected
        parser.doesNotMatch(0x1, 0x2, 0, 0x2) {
            failAt(2)
            expectLiteral(0x1)
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x1, 0x2, 0) {
            failAt(4)
            expectEndOfInput()
        }
    }
}