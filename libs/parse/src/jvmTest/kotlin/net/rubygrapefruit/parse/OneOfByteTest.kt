package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.oneOf
import kotlin.test.Test

class OneOfByteTest : AbstractParseTest() {
    @Test
    fun `matches one of a set of bytes`() {
        val parser = oneOf(0x1, 0x2)

        parser.expecting {
            expectOneOf(0x1, 0x2)
        }

        parser.matches(0x1, expected = 0x1)
        parser.matches(0x2, expected = 0x2)

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x11) {
            expectLiteral(0x1)
            expectLiteral(0x2)
        }

        // extra
        parser.doesNotMatch(0x1, 0x1) {
            failAt(1)
            expectEndOfInput()
        }
    }
}