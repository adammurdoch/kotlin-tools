package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.match
import net.rubygrapefruit.parse.combinators.describedAs
import kotlin.test.Test

class DescribedAsOfMatchedTest : AbstractParseTest() {
    @Test
    fun `replaces expectation of match of binary literal`() {
        val parser = describedAs(match(literal(byteArrayOf(0x1, 0x2), 1)), "<byte>")

        parser.expecting {
            expectDescribed("<byte>") {
                expectMatch {
                    expectLiteral(0x1, 0x2)
                }
            }
        }

        parser.matches(0x1, 0x2, expected = byteArrayOf(0x1, 0x2))

        // missing
        parser.doesNotMatch {
            expect("<byte>")
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expect("<byte>")
        }
        parser.doesNotMatch(0x1, 0x3) {
            failAt(1)
            expectLiteral(0x2)
        }
    }
}