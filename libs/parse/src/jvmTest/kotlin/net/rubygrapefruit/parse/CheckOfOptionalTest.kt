package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.MappingResult
import net.rubygrapefruit.parse.combinators.check
import net.rubygrapefruit.parse.combinators.optional
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class CheckOfOptionalTest : AbstractParseTest() {
    @Test
    fun `maps optional char literal`() {
        val parser = check(
            optional(
                literal("abc", 1)
            )
        ) { MappingResult.of("[$it]") }

        parser.expecting {
            expectCheck {
                expectChoice {
                    expectLiteral("abc", result = 1)
                    expectSucceed(result = null)
                }
            }
        }

        parser.matches("abc", expected = "[1]")
        parser.matches("", expected = "[null]")

        // missing
        parser.doesNotMatch("ab") {
            expectLiteral("abc")
            expectEndOfInput()
        }

        // extra
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectEndOfInput()
        }
    }
}