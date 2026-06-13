package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.MappingResult
import net.rubygrapefruit.parse.combinators.check
import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.oneInRange
import kotlin.test.Test

class DescribedAsOfCheckTest : AbstractParseTest() {
    @Test
    fun `replaces expectation of check of n chars`() {
        val check = check(repeat(2, oneInRange('a'..'z'))) {
            if (it.first() == 'a') {
                MappingResult.of(1)
            } else {
                MappingResult.expected("<ignored>")
            }
        }
        val parser = describedAs(check, "<chars>")

        parser.expecting {
            expectDescribed("<chars>") {
                expectCheck {
                    expectRepeat(2) {
                        expectOneInRange('a', 'z')
                    }
                }
            }
        }

        parser.matches("az", expected = 1)

        // does not match predicate
        parser.doesNotMatch("bz") {
            expect("<chars>")
        }

        // missing
        parser.doesNotMatch("") {
            expect("<chars>")
        }
        parser.doesNotMatch("a5") {
            failAt(1)
            expectOneInRange('a', 'z')
        }

        // unexpected
        parser.doesNotMatch("1c") {
            expect("<chars>")
        }
        parser.doesNotMatch("b5") {
            failAt(1)
            expectOneInRange('a', 'z')
        }
    }
}