package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.oneOf
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class ChoiceOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches choice of zero or more of a set of bytes`() {
        val parser = oneOf(
            zeroOrMore(oneOf(0x1, 0x2)),
            zeroOrMore(oneOf(0x10, 0x11))
        )

        parser.matches(expected = emptyList())

        parser.matches(0x1, expected = listOf(0x1))
        parser.matches(0x11, expected = listOf(0x11))

        parser.matches(0x1, 0x2, 0x1, expected = listOf(0x1, 0x2, 0x1))
        parser.matches(0x11, 0x10, 0x11, expected = listOf(0x11, 0x10, 0x11))

        // unexpected
        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expect("x01")
            expect("x02")
            expect("x10")
            expect("x11")
        }
        parser.doesNotMatch(0x1, 0x11) {
            failAt(1)
            expectEndOfInput()
            expect("x01")
            expect("x02")
        }
        parser.doesNotMatch(0x10, 0x2) {
            failAt(1)
            expectEndOfInput()
            expect("x10")
            expect("x11")
        }
    }
}