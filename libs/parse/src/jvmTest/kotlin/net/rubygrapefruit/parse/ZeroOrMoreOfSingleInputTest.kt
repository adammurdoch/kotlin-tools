package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class ZeroOrMoreOfSingleInputTest : AbstractParseTest() {
    @Test
    fun `matches zero or more of one of char`() {
        val parser = zeroOrMore(
            oneOf('a', 'b')
        )

        parser.expecting {
            emptyMatch()
            expectLiteral("a")
            expectLiteral("b")
            expectIsZeroOrMoreSingleInput()
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

    @Test
    fun `matches zero or more of one of byte`() {
        val parser = zeroOrMore(
            oneOf(0x1, 0x2)
        )

        parser.expecting {
            emptyMatch()
            expectLiteral(0x1)
            expectLiteral(0x2)
            expectIsZeroOrMoreSingleInput()
        }

        parser.matches(expected = emptyList())
        parser.matches(0x1, expected = bytes(0x1))
        parser.matches(0x2, expected = bytes(0x2))
        parser.matches(0x2, 0x1, 0x1, expected = bytes(0x2, 0x1, 0x1))

        // unexpected
        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x2, 0x1, 0x3) {
            failAt(2)
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
    }
}