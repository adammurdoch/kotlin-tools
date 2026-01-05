package net.rubygrapefruit.parse

import kotlin.test.Test

class ByteLiteralTest: AbstractParseTest() {
    @Test
    fun `matches single byte literal`() {
        val parser = literal(0x1)

        matches(parser, 0x1)

        // missing
        doesNotMatch(parser)

        // unexpected
        doesNotMatch(parser, 0x2)

        // extra
        doesNotMatch(parser, 0x1, 0x2)
    }

    @Test
    fun `matches multi-byte literal`() {
        val parser = literal(0x1, 0x2)

        matches(parser, 0x1, 0x2)

        // missing
        doesNotMatch(parser)
        doesNotMatch(parser, 0x1)

        // unexpected
        doesNotMatch(parser, 0x3)
        doesNotMatch(parser, 0x3, 0x1)
        doesNotMatch(parser, 0x3, 0x1, 0x2)
        doesNotMatch(parser, 0x1, 0x3)
        doesNotMatch(parser, 0x1, 0x3, 0x2)

        // extra
        doesNotMatch(parser, 0x1, 0x2, 0x3)
    }
}