package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.not
import net.rubygrapefruit.parse.combinators.optional
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class NotOfOptionalTest : AbstractParseTest() {
    @Test
    fun `never matches`() {
        val parser = not(optional(literal("abc", 7)))

        parser.expecting {
            expectNot {
                expectChoice {
                    expectLiteral("abc")
                    expectSucceed()
                }
            }
        }

        parser.doesNotMatch("") {
            expect("end of input")
            expect("not \"abc\"")
        }
        parser.doesNotMatch("X") {
            expect("end of input")
            expect("not \"abc\"")
        }
    }
}