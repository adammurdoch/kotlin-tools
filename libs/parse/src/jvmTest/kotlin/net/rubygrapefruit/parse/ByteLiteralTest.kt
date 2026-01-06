package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.literal
import kotlin.test.Test

class ByteLiteralTest : AbstractParseTest() {
    @Test
    fun `matches single byte literal`() {
        val parser = literal(byteArrayOf(0x1))

        matches(parser, 0x1)

        // missing
        doesNotMatch(parser) {
            expect("x01")
        }

        // unexpected
        doesNotMatch(parser, 0x2) {
            expect("x01")
        }

        // extra
        doesNotMatch(parser, 0x1, 0x2) {
            failAt(1)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches single byte literal and produces result`() {
        val parser = literal(byteArrayOf(0x1), result = "one")

        matches(parser, 0x1, expected = "one")
    }

    @Test
    fun `matches multi-byte literal`() {
        val parser = literal(byteArrayOf(0x1, 0x2))

        matches(parser, 0x1, 0x2)

        // missing
        doesNotMatch(parser) {
            expect("x01, x02")
        }
        doesNotMatch(parser, 0x1) {
            expect("x01, x02")
        }

        // unexpected
        doesNotMatch(parser, 0x3) {
            expect("x01, x02")
        }
        doesNotMatch(parser, 0x3, 0x1) {
            expect("x01, x02")
        }
        doesNotMatch(parser, 0x3, 0x1, 0x2) {
            expect("x01, x02")
        }
        doesNotMatch(parser, 0x1, 0x3) {
            expect("x01, x02")
        }
        doesNotMatch(parser, 0x1, 0x3, 0x2) {
            expect("x01, x02")
        }

        // extra
        doesNotMatch(parser, 0x1, 0x2, 0x3) {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches multi-byte literal and produces result`() {
        val parser = literal(byteArrayOf(0x1, 0x2), "one-two")

        matches(parser, 0x1, 0x2, expected = "one-two")
    }
}