package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.oneInRange
import kotlin.test.Test

class ZeroOrMoreOfDescribedAsTest : AbstractParseTest() {
    @Test
    fun `matches zero or more of char in range`() {
        val parser = zeroOrMore(describedAs(oneInRange('a'..'z'), "<char>"))

        parser.expecting {
            expectZeroOrMoreSingleInput(expect = "<char>") {
                expectOneInRange('a', 'z', hasResult = false)
            }
        }

        parser.matches("", expected = emptyList())
        parser.matches("ab", expected = listOf('a', 'b'))

        parser.doesNotMatch("X") {
            expect("<char>")
            expectEndOfInput()
        }
        parser.doesNotMatch("aX") {
            failAt(1)
            expect("<char>")
            expectEndOfInput()
        }
    }
}