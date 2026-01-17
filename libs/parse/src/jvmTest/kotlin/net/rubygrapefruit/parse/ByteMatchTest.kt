package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.match
import kotlin.test.Test

class ByteMatchTest : AbstractParseTest() {
    @Test
    fun `matches byte literal and produces matching input`() {
        val parser = match(literal(byteArrayOf(0x1, 0x2)))

        parser.expecting {
            expectLiteral(0x1)
        }

        parser.matches(0x1, 0x2, expected = byteArrayOf(0x1, 0x2))

        parser.doesNotMatch {
            expectLiteral(0x1)
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
        }
    }
}