package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import kotlin.test.Test

class ByteLiteralTest : AbstractParseTest() {
    @Test
    fun `matches single byte literal`() {
        val parser = literal(byteArrayOf(0x1))

        parser.expecting {
            // don't use expectLiteral() here, to check formatting
            expect("x01")
        }

        parser.matches(0x1)

        // missing
        parser.doesNotMatch {
            expect("x01")
        }

        // unexpected
        parser.doesNotMatch(0x2) {
            expect("x01")
        }

        // extra
        parser.doesNotMatch(0x1, 0x2) {
            failAt(1)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches single byte literal and produces result`() {
        val parser = literal(byteArrayOf(0x1), result = "one")

        parser.matches(0x1, expected = "one")
    }

    @Test
    fun `matches multi-byte literal`() {
        val parser = literal(byteArrayOf(0x1, 0x2))

        parser.expecting {
            // don't use expectLiteral() here, to check formatting
            expect("x01")
        }

        parser.matches(0x1, 0x2)

        // missing
        parser.doesNotMatch {
            expect("x01")
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expect("x02")
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expect("x01")
        }
        parser.doesNotMatch(0x3, 0x1) {
            expect("x01")
        }
        parser.doesNotMatch(0x3, 0x1, 0x2) {
            expect("x01")
        }
        parser.doesNotMatch(0x1, 0x3) {
            failAt(1)
            expect("x02")
        }
        parser.doesNotMatch(0x1, 0x3, 0x2) {
            failAt(1)
            expect("x02")
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches multi-byte literal and produces result`() {
        val parser = literal(byteArrayOf(0x1, 0x2), "one-two")

        parser.matches(0x1, 0x2, expected = "one-two")
    }
}