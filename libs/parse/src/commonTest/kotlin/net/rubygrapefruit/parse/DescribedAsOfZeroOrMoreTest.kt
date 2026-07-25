package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.oneInRange
import kotlin.test.Test

class DescribedAsOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `replaces expectation of zero or more char in range`() {
        val parser = describedAs(zeroOrMore(oneInRange('a'..'z')), "<chars>")

        parser.expecting {
            expectDescribed("<chars>") {
                expectZeroOrMoreSingleInput {
                    expectOneInRange('a', 'z')
                }
            }
        }

        parser.matches("", expected = emptyList())
        parser.matches("abc", expected = listOf('a', 'b', 'c'))

        // unexpected
        parser.doesNotMatch("X") {
            expect("<chars>")
            expectEndOfInput()
        }
        parser.doesNotMatch("aX") {
            failAt(1)
            expectOneInRange('a', 'z')
            expectEndOfInput()
        }
    }
}