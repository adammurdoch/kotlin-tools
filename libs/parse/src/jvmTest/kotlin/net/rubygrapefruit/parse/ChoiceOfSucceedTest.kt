package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.general.succeed
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ChoiceOfSucceedTest : AbstractParseTest() {
    @Test
    fun `matches succeed parser`() {
        val parser = oneOf(
            literal("ab", 1),
            succeed(2)
        )

        parser.expecting {
            emptyMatch()
            expectChoice {
                expectLiteral("ab", result = 1)
                expectSucceed(result = 2)
            }
        }

        parser.matches("ab", expected = 1)
        parser.matches("", expected = 2)

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("ab")
            expectEndOfInput()
        }

        // unexpected
        parser.doesNotMatch("aX") {
            expectLiteral("ab")
            expectEndOfInput()
        }

        // extra
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
        parser.doesNotMatch("X") {
            expectLiteral("ab")
            expectEndOfInput()
        }
    }

    @Test
    fun `succeed parser hides subsequent options`() {
        val parser = oneOf(
            succeed(2),
            literal("ab", 1)
        )

        parser.expecting {
            emptyMatch()
            expectChoice {
                expectSucceed(result = 2)
                expectLiteral("ab", result = 1)
            }
        }

        parser.matches("", expected = 2)

        // hidden
        parser.doesNotMatch("ab") {
            expectEndOfInput()
        }

        // missing
        parser.doesNotMatch("a") {
            expectEndOfInput()
        }

        // extra
        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
    }
}