package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class DiscardOfOneOfSetTest : AbstractParseTest() {
    @Test
    fun `discards the result of one of char`() {
        val parser = discard(oneOf('a', 'b'))

        parser.expecting {
            expectOneOf('a', 'b', hasResult = false)
        }

        parser.matches("a")
        parser.matches("b")

        parser.doesNotMatch("") {
            expectLiteral("a")
            expectLiteral("b")
        }
    }

    @Test
    fun `discards the result of one of byte`() {
        val parser = discard(oneOf(0x1, 0x2))

        parser.expecting {
            expectOneOf(0x1, 0x2, hasResult = false)
        }

        parser.matches(0x1)
        parser.matches(0x2)

        parser.doesNotMatch {
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
    }
}