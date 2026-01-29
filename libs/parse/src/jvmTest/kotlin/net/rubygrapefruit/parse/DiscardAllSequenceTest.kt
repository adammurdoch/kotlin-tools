package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardAllSequenceTest : AbstractParseTest() {
    @Test
    fun `matches literal then literal`() {
        val parser = sequence(literal("a"), literal("b"))

        parser.expecting {
            expectSequence {
                expectLiteral("a")
                expectLiteral("b")
            }
        }

        parser.matches("ab")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
    }

    @Test
    fun `matches literal then literal then literal`() {
        val parser = sequence(literal("a"), literal("b"), literal("c"))

        parser.expecting {
            expectSequence {
                expectLiteral("a")
                expectSequence {
                    expectLiteral("b")
                    expectLiteral("c")
                }
            }
        }

        parser.matches("abc")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
    }
}