package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import kotlin.test.Test

class BinaryLiteralTest : AbstractParseTest() {
    @Test
    fun `matches single byte literal`() {
        val parser = literal(byteArrayOf(0x1))

        parser.expecting {
            expectLiteral(0x1)
        }

        parser.matches(0x1) {
            steps {
                advance(1)
            }
        }

        // missing
        parser.doesNotMatch {
            // don't use expectLiteral() here, to check formatting
            expect("x01")
            steps {}
        }

        // unexpected
        parser.doesNotMatch(0x2) {
            expect("x01")
            steps {}
        }

        // extra
        parser.doesNotMatch(0x1, 0x2) {
            failAt(1)
            expectEndOfInput()
            steps {
                advance(1)
            }
        }
    }

    @Test
    fun `formats expected bytes`() {
        val candidates = listOf(
            0 to "x0",
            0x1 to "x01",
            0xf to "x0F",
            0xFF to "xFF"
        )
        for (candidate in candidates) {
            val literal = candidate.first.toByte()
            val parser = literal(byteArrayOf(literal))

            parser.matches(literal)

            // missing
            parser.doesNotMatch {
                expect(candidate.second)
            }
        }
    }

    @Test
    fun `matches single byte literal and produces result`() {
        val parser = literal(byteArrayOf(0x1), result = "one")

        parser.expecting {
            expectLiteral(0x1, result = "one")
        }

        parser.matches(0x1, expected = "one")
    }

    @Test
    fun `matches single byte literal provided as a byte`() {
        val parser = literal(0x1)

        parser.expecting {
            expectLiteral(0x1)
        }

        parser.matches(0x1) {
            steps {
                advance(1)
            }
        }

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            steps {}
        }

        // unexpected
        parser.doesNotMatch(0x2) {
            expectLiteral(0x1)
            steps {}
        }

        // extra
        parser.doesNotMatch(0x1, 0x2) {
            failAt(1)
            expectEndOfInput()
            steps {
                advance(1)
            }
        }
    }

    @Test
    fun `matches single byte literal provided as byte and produces result`() {
        val parser = literal(0x1, result = "one")

        parser.expecting {
            expectLiteral(0x1, result = "one")
        }

        parser.matches(0x1, expected = "one")
    }

    @Test
    fun `matches multi-byte literal`() {
        val parser = literal(byteArrayOf(0x1, 0x2))

        parser.expecting {
            expectLiteral(0x1, 0x2)
        }

        parser.matches(0x1, 0x2) {
            steps {
                advance(2)
            }
        }

        // missing
        parser.doesNotMatch {
            // don't use expectLiteral() here, to check formatting
            expect("x01")
            steps {}
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expect("x02")
            steps {
                advance(1)
            }
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expect("x01")
            steps {}
        }
        parser.doesNotMatch(0x3, 0x1) {
            expect("x01")
            steps {}
        }
        parser.doesNotMatch(0x3, 0x1, 0x2) {
            expect("x01")
            steps {}
        }
        parser.doesNotMatch(0x1, 0x3) {
            failAt(1)
            expect("x02")
            steps {}
        }
        parser.doesNotMatch(0x1, 0x3, 0x2) {
            failAt(1)
            expect("x02")
            steps {}
        }

        // extra
        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(2)
            expectEndOfInput()
            steps {
                advance(2)
            }
        }
    }

    @Test
    fun `matches multi-byte literal and produces result`() {
        val parser = literal(byteArrayOf(0x1, 0x2), "one-two")

        parser.expecting {
            expectLiteral(0x1, 0x2, result = "one-two")
        }

        parser.matches(0x1, 0x2, expected = "one-two")
    }
}