package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardOfDescribedAsTest : AbstractParseTest() {
    @Test
    fun `discards result of char literal with description`() {
        val parser = discard(describedAs(literal("abc", 2), "<literal>"))

        parser.expecting {
            expectDescribed("<literal>") {
                expectLiteral("abc", result = Unit)
            }
        }

        parser.matches("abc")

        parser.doesNotMatch("") {
            expect("<literal>")
        }
    }
}