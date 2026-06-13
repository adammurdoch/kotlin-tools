package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.combinators.map
import kotlin.test.Test

class DescribedAsOfMapTest : AbstractParseTest() {
    @Test
    fun `replaces expectation of map of binary literal`() {
        val parser = describedAs(map(literal(byteArrayOf(0x1, 0x2), 1)) { it + 1 }, "<byte>")

        parser.expecting {
            expectDescribed("<byte>") {
                expectMap {
                    expectLiteral(0x1, 0x2, result = 1)
                }
            }
        }

        parser.matches(0x1, 0x2, expected = 2)

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