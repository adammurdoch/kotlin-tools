package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.oneOf
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
            expectLiteral("ab")
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
}