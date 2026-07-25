package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.decide
import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test
import kotlin.test.fail

class DiscardOfDecideTest : AbstractParseTest() {
    @Test
    fun `discards result of decide parser`() {
        val literal = literal("a.", 2)
        val parser = discard(
            decide(
                literal
            ) {
                sequence(
                    literal,
                    literal
                ) { _, _ -> fail() }
            }
        )

        parser.expecting {
            expectDecide {
                expectLiteral("a.", result = 2)
                expectSequence {
                    expectLiteral("a.", result = Unit)
                    expectLiteral("a.", result = Unit)
                }
            }
        }

        parser.matches("a.a.a.")

        parser.doesNotMatch("a.X") {
            failAt(2)
            expectLiteral("a.")
        }
    }
}