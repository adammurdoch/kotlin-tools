package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DescribedAsOfRepeatTest : AbstractParseTest() {
    @Test
    fun `replaces expectation of repeat of text literal`() {
        val parser = describedAs(repeat(2, literal("a,")), "<chars>")

        parser.expecting {
            expectDescribed("<chars>") {
                expectRepeat(2, hasResult = false) {
                    expectLiteral("a,")
                }
            }
        }

        parser.matches("a,a,")

        // missing
        parser.doesNotMatch("") {
            expect("<chars>")
        }
        parser.doesNotMatch("a") {
            expect("<chars>")
        }
        parser.doesNotMatch("a,") {
            failAt(2)
            expectLiteral("a,")
        }
        parser.doesNotMatch("a,a") {
            failAt(2)
            expectLiteral("a,")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expect("<chars>")
        }
        parser.doesNotMatch("aX") {
            expect("<chars>")
        }
        parser.doesNotMatch("a,X") {
            failAt(2)
            expectLiteral("a,")
        }
    }
}