package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import kotlin.test.Test

class SequenceOfOneOfSetTest : AbstractParseTest() {
    @Test
    fun `matches one of set of byte then one of set of byte`() {
        val parser = sequence(
            oneOf(0x1, 0x2),
            oneOf(0x10, 0x11)
        ) { a, b -> listOf(a, b) }

        parser.matches(0x1, 0x11, expected = listOf(0x1, 0x11))
        parser.matches(0x2, 0x10, expected = listOf(0x2, 0x10))

        // missing
        parser.doesNotMatch {
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x1) {
            failAt(1)
            expectLiteral(0x10)
            expectLiteral(0x11)
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x2, 0x3) {
            failAt(1)
            expectLiteral(0x10)
            expectLiteral(0x11)
        }

        // extra
        parser.doesNotMatch(0x2, 0x11, 0x3) {
            failAt(2)
            expectEndOfInput()
        }
    }
}