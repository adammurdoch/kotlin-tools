package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.one
import kotlin.test.Test

class ZeroOrMoreOfOneCharTest : AbstractParseTest() {
    @Test
    fun `matches zero or more of one char`() {
        val parser = zeroOrMore(
            one()
        )

        parser.expecting {
            expectZeroOrMoreSingleInput {
                expectOneChar()
            }
        }

        parser.matches("", expected = emptyList())
        parser.matches("a", expected = listOf('a'))
        parser.matches("b", expected = listOf('b'))
        parser.matches("baa", expected = listOf('b', 'a', 'a'))
    }
}