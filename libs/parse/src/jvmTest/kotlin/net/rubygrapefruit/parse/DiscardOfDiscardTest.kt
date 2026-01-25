package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardOfDiscardTest : AbstractParseTest() {
    @Test
    fun `discards the result of char literal`() {
        val parser = discard(
            discard(
                literal("abc", 1)
            )
        )

        parser.expecting {
            expectLiteral("abc", result = Unit)
        }

        parser.matches("abc")

        parser.doesNotMatch("") {
            expectLiteral("abc")
        }
    }
}