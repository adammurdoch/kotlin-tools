package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.decide
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DecideTest : AbstractParseTest() {
    @Test
    fun `produces char literal parser from char literal`() {
        val parser = decide(literal("ab", 2)) { literal(it.toString()) }

        parser.expecting {
            expectDecide {
                expectLiteral("ab", result = 2)
                expectLiteral("2")
            }
        }

        parser.matches("ab2")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("ab")
        }
        parser.doesNotMatch("ab") {
            failAt(2)
            expectLiteral("2")
        }
    }
}