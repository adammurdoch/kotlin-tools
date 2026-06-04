package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.MappingResult
import net.rubygrapefruit.parse.combinators.check
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardOfCheckTest : AbstractParseTest() {
    @Test
    fun `discards result of map of literal`() {
        val check = check(literal("abc", 1)) { MappingResult.of(it + 1) }
        val parser = discard(check)

        parser.expecting {
            expectCheck {
                expectLiteral("abc", result = 1)
            }
        }

        parser.matches("abc")

        // unexpected
        parser.doesNotMatch("abX") {
            expectLiteral("abc")
        }
    }

    @Test
    fun `mapping function can reject value`() {
        val check = check(literal("abc", 1)) { MappingResult.expected("not $it") }
        val parser = discard(check)

        parser.doesNotMatch("abc") {
            expect("not 1")
        }

        // unexpected
        parser.doesNotMatch("abX") {
            expectLiteral("abc")
        }
    }
}