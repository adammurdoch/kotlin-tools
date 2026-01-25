package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.not
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardOfNotTest : AbstractParseTest() {
    @Test
    fun `discards result of not`() {
        val parser = discard(not(literal("abc", 1)))

        parser.expecting {
            expectNot {
                expectLiteral("abc", result = Unit)
            }
        }

        parser.matches("")

        parser.doesNotMatch("X") {
            expectEndOfInput()
            expect("not \"abc\"")
        }
    }
}