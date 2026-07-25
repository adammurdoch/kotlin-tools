package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.suffixed
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test
import kotlin.test.fail

class SuffixedOfSequenceTest : AbstractParseTest() {
    @Test
    fun `discards the result of first sequence`() {
        val parser = suffixed(
            literal("!", 1),
            sequence(oneOf('a', 'b'), oneOf('1', '2')) { _, _ -> fail() }
        )

        parser.expecting {
            expectSequence {
                expectLiteral("!", result = 1)
                expectSequence {
                    expectOneOf('a', 'b', hasResult = false)
                    expectOneOf('1', '2', hasResult = false)
                }
            }
        }

        parser.matches("!a2", expected = 1)
        parser.matches("!b1", expected = 1)

        parser.doesNotMatch("!a") {
            failAt(2)
            expectLiteral("1")
            expectLiteral("2")
        }
    }
}