package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.optional
import kotlin.test.Test

class DiscardOfOptionalTest : AbstractParseTest() {
    @Test
    fun `discards result of optional byte literal`() {
        val parser = discard(optional(literal(byteArrayOf(0x1, 0x2), 1), 0))

        parser.expecting {
            expectChoice {
                expectLiteral(0x1, 0x2, result = Unit)
                expectSucceed(result = Unit)
            }
        }

        parser.matches(0x1, 0x2)
        parser.matches()

        // extra
        parser.doesNotMatch(0x3) {
            expectLiteral(0x1)
            expectEndOfInput()
        }
    }
}