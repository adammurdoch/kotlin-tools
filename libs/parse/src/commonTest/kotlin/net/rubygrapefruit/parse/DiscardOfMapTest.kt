package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test
import kotlin.test.fail

class DiscardOfMapTest : AbstractParseTest() {
    @Test
    fun `discards result of map of literal`() {
        val map = map(literal("ab", 1)) { fail() }
        val parser = discard(map)

        parser.expecting {
            expectLiteral("ab", result = Unit)
        }

        parser.matches("ab")

        parser.doesNotMatch("") {
            expectLiteral("ab")
        }
    }
}