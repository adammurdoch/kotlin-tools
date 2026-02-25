package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardOfOneOrMoreTest : AbstractParseTest() {
    @Test
    fun `discards result of one or more of char literal`() {
        val parser = discard(
            oneOrMore(
                literal("a.", 1)
            )
        )

        parser.expecting {
            expectOneOrMore(hasResult = false) {
                expectLiteral("a.", result = Unit)
            }
        }

        parser.matches("a.")
        parser.matches("a.a.a.")

        parser.doesNotMatch("a.aX") {
            failAt(2)
            expectLiteral("a.")
            expectEndOfInput()
        }
    }

    @Test
    fun `discards result of one or more of char literal with no result`() {
        val parser = discard(
            oneOrMore(
                literal("a.")
            )
        )

        parser.expecting {
            expectOneOrMore(hasResult = false) {
                expectLiteral("a.", result = Unit)
            }
        }

        parser.matches("a.")
        parser.matches("a.a.a.")

        parser.doesNotMatch("a.aX") {
            failAt(2)
            expectLiteral("a.")
            expectEndOfInput()
        }
    }

    @Test
    fun `discards result of one or more of char literal with separator`() {
        val parser = discard(
            oneOrMore(
                literal("a", 1),
                literal(".", 2)
            )
        )

        parser.expecting {
            expectOneOrMore(hasResult = false) {
                expectLiteral("a", result = Unit)
                expectLiteral(".", result = Unit)
            }
        }

        parser.matches("a")
        parser.matches("a.a.a")

        parser.doesNotMatch("a.aX") {
            failAt(3)
            expectLiteral(".")
            expectEndOfInput()
        }
    }
}