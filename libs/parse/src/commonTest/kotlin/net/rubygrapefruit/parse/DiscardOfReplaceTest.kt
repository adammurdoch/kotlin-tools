package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.replace
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardOfReplaceTest : AbstractParseTest() {
    @Test
    fun `discards result of replace parser`() {
        val parser = discard(
            replace(
                literal("ab", 2),
                4
            )
        )

        parser.expecting {
            expectLiteral("ab", result = Unit)
        }

        parser.matches("ab")

        parser.doesNotMatch("") {
            expectLiteral("ab")
        }
    }
}