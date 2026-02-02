package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.decide
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class ChoiceOfDecideTest : AbstractParseTest() {
    @Test
    fun `matches choice of decide with common prefix`() {
        val parser = oneOf(
            decide(literal("a", 1)) { literal(it.toString()) },
            decide(literal("a", 2)) { literal(it.toString()) }
        )

        parser.matches("a1")
        parser.matches("a2")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("1")
            expectLiteral("2")
        }
    }
}