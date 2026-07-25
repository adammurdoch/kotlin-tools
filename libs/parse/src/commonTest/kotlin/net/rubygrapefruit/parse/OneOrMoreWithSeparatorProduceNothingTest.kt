package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.oneOrMore
import kotlin.test.Test

class OneOrMoreWithSeparatorProduceNothingTest : AbstractParseTest() {
    @Test
    fun `matches one or more of binary literal with separator`() {
        val parser = oneOrMore(literal(byteArrayOf(0x1, 0x2)), oneOf(0x3, 0x4))

        parser.expecting {
            expectOneOrMore(hasResult = false) {
                expectLiteral(0x1, 0x2)
                expectOneOf(0x3, 0x4, hasResult = false)
            }
        }

        parser.matches(0x1, 0x2)
        parser.matches(0x1, 0x2, 0x3, 0x1, 0x2, 0x4, 0x1, 0x2)

        // missing
        parser.doesNotMatch(0x1, 0x2, 0x4) {
            failAt(3)
            expectLiteral(0x1)
        }

        // prefix missing
        parser.doesNotMatch(0x3, 0x1, 0x2) {
            expectLiteral(0x1)
        }

        // separator missing
        parser.doesNotMatch(0x1, 0x2, 0x1, 0x2) {
            failAt(2)
            expectLiteral(0x3)
            expectLiteral(0x4)
            expectEndOfInput()
        }

        // unexpected
        parser.doesNotMatch(0x1, 0x2, 0x10) {
            failAt(2)
            expectLiteral(0x3)
            expectLiteral(0x4)
            expectEndOfInput()
        }
    }
}