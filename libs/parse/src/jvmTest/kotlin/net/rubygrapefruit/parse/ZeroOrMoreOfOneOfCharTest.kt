package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class ZeroOrMoreOfOneOfCharTest : AbstractParseTest() {
    @Test
    fun `matches zero or more of one of char`() {
        val parser = zeroOrMore(
            oneOf('a', 'b')
        )

        parser.expecting {
            emptyMatch()
            expectLiteral("a")
            expectLiteral("b")
        }

        parser.matches("", expected = emptyList())
        parser.matches("a", expected = listOf('a'))
        parser.matches("b", expected = listOf('b'))
        parser.matches("baa", expected = listOf('b', 'a', 'a'))

        // unexpected
        parser.doesNotMatch("1") {
            expectLiteral("a")
            expectLiteral("b")
            expectEndOfInput()
        }
        parser.doesNotMatch("ba1") {
            failAt(2)
            expectLiteral("a")
            expectLiteral("b")
            expectEndOfInput()
        }
    }
}