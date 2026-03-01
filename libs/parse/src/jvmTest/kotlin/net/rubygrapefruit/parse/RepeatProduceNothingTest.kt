package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.repeat
import kotlin.test.Test

class RepeatProduceNothingTest : AbstractParseTest() {
    @Test
    fun `parses n byte literals`() {
        val parser = repeat(3, literal(byteArrayOf(0x1, 0x2)))

        parser.expecting {
            expectRepeat(3, hasResult = false) {
                expectLiteral(0x1, 0x2)
            }
        }

        parser.matches(0x1, 0x2, 0x1, 0x2, 0x1, 0x2)

        // missing
        parser.doesNotMatch(0x1, 0x2) {
            failAt(2)
            expectLiteral(0x1)
        }
        parser.doesNotMatch(0x1, 0x2, 0x1, 0x2, 0x1) {
            failAt(5)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x2, 0x1, 0x2) {
            failAt(2)
            expectLiteral(0x1)
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x1, 0x2, 0x1, 0x2, 0x1, 0x2) {
            failAt(6)
            expectEndOfInput()
        }
    }
}