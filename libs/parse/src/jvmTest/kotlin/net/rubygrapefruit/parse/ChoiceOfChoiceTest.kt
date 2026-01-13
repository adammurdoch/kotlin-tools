package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.char.literal
import net.rubygrapefruit.parse.combinators.oneOf
import kotlin.test.Test

class ChoiceOfChoiceTest : AbstractParseTest() {
    @Test
    fun `matches choice of choice of literals`() {
        val parser = oneOf(
            oneOf(
                literal("abc", 1),
                literal("ad", 2)
            ),
            oneOf(
                literal("ab", 3),
                literal("a", 4)
            )
        )

        parser.expecting {
            expectLiteral("a")
            expectLiteral("ab")
            expectLiteral("abc")
            expectLiteral("ad")
        }

        parser.matches("abc", expected = 1)
        parser.matches("ad", expected = 2)
        parser.matches("ab", expected = 3)
        parser.matches("a", expected = 4)

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
            expectLiteral("ab")
            expectLiteral("abc")
            expectLiteral("ad")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
            expectLiteral("ab")
            expectLiteral("abc")
            expectLiteral("ad")
        }

        // extra
        parser.doesNotMatch("aX") {
            failAt(1)
            expectEndOfInput()
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
        parser.doesNotMatch("adX") {
            failAt(2)
            expectEndOfInput()
        }
    }
}