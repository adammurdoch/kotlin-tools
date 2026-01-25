package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.not
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test
import kotlin.test.fail

class NotOfSequenceTest : AbstractParseTest() {
    @Test
    fun `discards result of sequence`() {
        val parser = not(
            sequence(
                literal("a", 1),
                literal("1", 2),
            ) { _, _ -> fail() }
        )

        parser.expecting {
            expectNot {
                expectSequence {
                    expectLiteral("a")
                    expectLiteral("1")
                }
            }
        }

        parser.matches("")

        parser.doesNotMatch("a1") {
            expectEndOfInput()
        }
        parser.doesNotMatch("aX") {
            expectEndOfInput()
        }
    }
}