package net.rubygrapefruit.parse

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
            expectChoice {
                expectOneOrMore(hasResult = false) {
                    expectLiteral("abc", result = Unit)
                }
                expectZero()
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
            expectChoice {
                expectOneOrMore(hasResult = false) {
                    expectLiteral("abc", result = Unit)
                }
                expectZero()
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
}