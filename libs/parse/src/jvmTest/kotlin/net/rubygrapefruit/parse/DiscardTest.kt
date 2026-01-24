package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardTest : AbstractParseTest() {
    @Test
    fun `discards the result of char literal`() {
        val parser = discard(literal("abc", 1))

        parser.expecting {
            expectLiteralNoResult("abc")
        }

        parser.matches("abc")

        parser.doesNotMatch("") {
            expectLiteral("abc")
        }
    }

    @Test
    fun `discards the result of byte literal`() {
        val parser = discard(literal(byteArrayOf(0x1, 0x2), 1))

        parser.expecting {
            expectLiteralNoResult(0x1)
        }

        parser.matches(0x1, 0x2)

        parser.doesNotMatch {
            expectLiteral(0x1)
        }
    }
}