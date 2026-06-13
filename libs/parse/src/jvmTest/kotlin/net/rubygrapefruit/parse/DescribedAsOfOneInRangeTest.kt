package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.oneInRange
import net.rubygrapefruit.parse.combinators.describedAs
import kotlin.test.Test

class DescribedAsOfOneInRangeTest : AbstractParseTest() {
    @Test
    fun `replaces expectation of byte in range`() {
        val parser = describedAs(oneInRange(0x1, 0x3), "<byte>")

        parser.expecting {
            expectDescribed("<byte>") {
                expectOneInRange(0x1, 0x3)
            }
        }

        parser.matches(0x2, expected = 0x2) {
            steps {
                advance(1)
            }
        }

        // missing
        parser.doesNotMatch {
            expect("<byte>")
        }

        // unexpected
        parser.doesNotMatch(0x0, 0x2) {
            expect("<byte>")
        }

        // extra
        parser.doesNotMatch(0x2, 0x2) {
            failAt(1)
            expectEndOfInput()
        }
    }
}