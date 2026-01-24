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
            expectLiteral("abc", result = Unit)
        }

        parser.matches("abc")

        parser.doesNotMatch("") {
            expectLiteral("abc")
        }
    }

    @Test
    fun `does nothing for char literal that does not produce a result`() {
        val parser = discard(literal("abc"))

        parser.expecting {
            expectLiteral("abc", result = Unit)
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
            expectLiteral(0x1, 0x2, result = Unit)
        }

        parser.matches(0x1, 0x2)

        parser.doesNotMatch {
            expectLiteral(0x1)
        }
    }
}