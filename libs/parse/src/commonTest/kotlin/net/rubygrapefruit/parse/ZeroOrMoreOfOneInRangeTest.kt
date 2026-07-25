package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.oneInRange
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.oneInRange
import kotlin.test.Test

class ZeroOrMoreOfOneInRangeTest : AbstractParseTest() {
    @Test
    fun `matches zero or more of char in range`() {
        val parser = zeroOrMore(oneInRange('a'..'z'))

        parser.expecting {
            expectZeroOrMoreSingleInput {
                expectOneInRange('a', 'z')
            }
        }

        parser.matches("", expected = listOf())
        parser.matches("abc", expected = listOf('a', 'b', 'c'))

        // unexpected
        parser.doesNotMatch("X") {
            expectOneInRange('a', 'z')
            expectEndOfInput()
        }
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectOneInRange('a', 'z')
            expectEndOfInput()
        }
    }

    @Test
    fun `matches zero or more of bytes in range`() {
        val parser = zeroOrMore(oneInRange(0x1, 0x3))

        parser.expecting {
            expectZeroOrMoreSingleInput {
                expectOneInRange(0x1, 0x3)
            }
        }

        parser.matches(expected = listOf())
        parser.matches(0x1, 0x2, 0x3, expected = bytes(0x1, 0x2, 0x3))

        // unexpected
        parser.doesNotMatch(0x4) {
            expectEndOfInput()
            expectOneInRange(0x1, 0x3)
        }
        parser.doesNotMatch(0x1, 0x2, 0x3, 0x4) {
            failAt(3)
            expectEndOfInput()
            expectOneInRange(0x1, 0x3)
        }
    }
}