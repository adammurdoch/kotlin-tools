package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.match
import kotlin.test.Test

class CharMatchOfChoiceTest : AbstractParseTest() {
    @Test
    fun `matches choice of literals`() {
        val parser = match(
            oneOf(
                literal("abc", 1),
                literal("12", 2)
            )
        )

        parser.expecting {
            expectMatch {
                expectChoice {
                    expectLiteral("12", result = 1)
                    expectLiteral("abc", result = 2)
                }
            }
        }

        parser.matches("abc", expected = "abc")
        parser.matches("12", expected = "12")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("12")
            expectLiteral("abc")
        }
    }
}