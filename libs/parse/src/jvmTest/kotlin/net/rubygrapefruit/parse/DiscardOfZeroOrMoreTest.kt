package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `discards result of zero or more of char literal`() {
        val parser = discard(
            zeroOrMore(
                literal("abc", 1)
            )
        )

        parser.expecting {
            expectZeroOrMore(hasResult = false) {
                expectLiteral("abc", result = Unit)
            }
        }

        parser.matches("")
        parser.matches("abcabc")

        parser.doesNotMatch("abcaX") {
            failAt(3)
            expectLiteral("abc")
            expectEndOfInput()
        }
    }

    @Test
    fun `discards result of zero or more of char literal with no result`() {
        val parser = discard(
            zeroOrMore(
                literal("abc")
            )
        )

        parser.expecting {
            expectZeroOrMore(hasResult = false) {
                expectLiteral("abc", result = Unit)
            }
        }

        parser.matches("")
        parser.matches("abcabc")

        parser.doesNotMatch("abcaX") {
            failAt(3)
            expectLiteral("abc")
            expectEndOfInput()
        }
    }

    @Test
    fun `discards result of zero or more of byte literal with separator`() {
        val parser = discard(
            zeroOrMore(
                literal(byteArrayOf(0x1, 0x2), 1),
                separator = literal(byteArrayOf(0x3))
            )
        )

        parser.expecting {
            expectZeroOrMore(hasResult = false) {
                expectLiteral(0x1, 0x2, result = Unit)
                expectLiteral(0x3, result = Unit)
            }
        }

        parser.matches()
        parser.matches(0x1, 0x2)
        parser.matches(0x1, 0x2, 0x3, 0x1, 0x2, 0x3, 0x1, 0x2)

        // missing
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x1, 0x2, 0x4) {
            failAt(2)
            expectLiteral(0x3)
            expectEndOfInput()
        }
    }
}