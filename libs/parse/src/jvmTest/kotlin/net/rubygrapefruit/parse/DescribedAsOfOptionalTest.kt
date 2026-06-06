package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.combinators.optional
import kotlin.test.Test

class DescribedAsOfOptionalTest : AbstractParseTest() {
    @Test
    fun `replaces expectation of optional byte literal`() {
        val parser = describedAs(optional(literal(byteArrayOf(0x1, 0x2), 1)), "thing")

        parser.expecting {
            expectDescribed("thing") {
                expectChoice {
                    expectLiteral(0x1, 0x2, result = 1)
                    expectSucceed(result = null)
                }
            }
        }

        parser.matches(expected = null)
        parser.matches(0x1, 0x2, expected = 1)

//        parser.doesNotMatch(0x3) {
//            expect("thing")
//            expectEndOfInput()
//        }

        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
        }

        parser.doesNotMatch(0x1, 0x2, 0x3) {
            failAt(2)
            expectEndOfInput()
        }
    }
}