package net.rubygrapefruit.parse


import net.rubygrapefruit.parse.combinators.consume
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test
import kotlin.test.fail

class DiscardOfConsumeTest : AbstractParseTest() {
    @Test
    fun `discards consumer`() {
        val parser = discard(consume(literal("123", 4)) { fail() })

        parser.expecting {
            expectLiteral("123", result = Unit)
        }

        parser.matches("123")

        parser.doesNotMatch("") {
            expectLiteral("123")
        }
    }
}