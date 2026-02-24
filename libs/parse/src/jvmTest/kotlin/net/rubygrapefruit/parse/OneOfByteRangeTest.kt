package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.oneInRange
import kotlin.test.Test

class OneOfByteRangeTest : AbstractParseTest() {
    @Test
    fun `matches one of a range of bytes`() {
        val parser = oneInRange(0x1, 0x3)

        parser.expecting {
            expectOneInRange(0x1, 0x3)
        }

        parser.matches(0x1, expected = 0x1)
        parser.matches(0x2, expected = 0x2)
        parser.matches(0x3, expected = 0x3)

        // missing
        parser.doesNotMatch {
            expect("x01..x03")
        }

        // unexpected
        parser.doesNotMatch(0x11) {
            expect("x01..x03")
        }

        // extra
        parser.doesNotMatch(0x1, 0x1) {
            failAt(1)
            expectEndOfInput()
        }
    }
}