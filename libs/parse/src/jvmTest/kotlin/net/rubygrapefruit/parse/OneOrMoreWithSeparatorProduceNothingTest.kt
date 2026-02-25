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
    }
}