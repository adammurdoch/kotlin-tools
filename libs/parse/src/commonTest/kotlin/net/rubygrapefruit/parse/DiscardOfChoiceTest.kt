package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardOfChoiceTest : AbstractParseTest() {
    @Test
    fun `discards result of choice of literals`() {
        val parser = discard(
            oneOf(
                literal("abc", 1),
                literal("12", 2)
            )
        )

        parser.expecting {
            expectChoice {
                expectLiteral("abc", result = Unit)
                expectLiteral("12", result = Unit)
            }
        }

        parser.matches("abc")
        parser.matches("12")

        parser.doesNotMatch("") {
            expectLiteral("abc")
            expectLiteral("12")
        }
    }
}