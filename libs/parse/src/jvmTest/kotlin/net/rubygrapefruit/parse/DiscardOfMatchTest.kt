package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.match
import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.zeroOrMore
import kotlin.test.Test

class DiscardOfMatchTest : AbstractParseTest() {
    @Test
    fun `discards result of match of zero or more of one of byte`() {
        val parser = discard(
            match(
                zeroOrMore(
                    oneOf(0x1, 0x2)
                )
            )
        )

        parser.expecting {
            expectZeroOrMoreSingleInput(hasResult = false) {
                expectOneOf(0x1, 0x2)
            }
        }

        parser.matches()
        parser.matches(0x1)
        parser.matches(0x2, 0x1, 0x2)

        // unexpected
        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
    }
}