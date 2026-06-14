package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.text.oneInRange
import kotlin.test.Test

class DescribedAsOfDescribedAsTest : AbstractParseTest() {
    @Test
    fun `replaces expectation of binary literal`() {
        val parser = describedAs(describedAs(literal(byteArrayOf(0x1, 0x2)), "<ignored>"), "<bytes>")

        parser.expecting {
            expectDescribed("<bytes>") {
                expectDescribed("<ignored>") {
                    expectLiteral(0x1, 0x2)
                }
            }
        }

        parser.matches(0x1, 0x2)

        // missing
        parser.doesNotMatch {
            expect("<bytes>")
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x2)
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expect("<bytes>")
        }
        parser.doesNotMatch(0x1, 0x3) {
            failAt(1)
            expectLiteral(0x2)
        }
    }

    @Test
    fun `replaces expectation of one or more chars in range`() {
        val parser = describedAs(describedAs(oneOrMore(oneInRange('a'..'z')), "<ignored>"), "<chars>")

        parser.expecting {
            expectDescribed("<chars>") {
                expectDescribed("<ignored>") {
                    expectOneOrMore {
                        expectOneInRange('a', 'z')
                    }
                }
            }
        }

        parser.matches("a", expected = listOf('a'))
        parser.matches("abc", expected = listOf('a', 'b', 'c'))

        // missing
        parser.doesNotMatch("") {
            expect("<chars>")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expect("<chars>")
        }
        parser.doesNotMatch("aX") {
            failAt(1)
            expectOneInRange('a', 'z')
            expectEndOfInput()
        }
    }
}