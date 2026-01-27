package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test
import kotlin.test.fail

class DiscardOfSequence3Test : AbstractParseTest() {
    @Test
    fun `discards result of sequence`() {
        val parser = discard(
            sequence(
                oneOf('a', 'b'),
                oneOf('1', '2'),
                oneOf(';', '!')
            ) { _, _, _ -> fail() }
        )

        parser.expecting {
            expectSequence {
                expectOneOf('a', 'b', hasResult = false)
                expectSequence {
                    expectOneOf('1', '2', hasResult = false)
                    expectOneOf(';', '!', hasResult = false)
                }
            }
        }

        parser.matches("a2;")
        parser.matches("b1!")

        parser.doesNotMatch("") {
            expectLiteral("a")
            expectLiteral("b")
        }
    }
}