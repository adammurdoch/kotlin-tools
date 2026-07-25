package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.prefixed
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test
import kotlin.test.fail

class PrefixedOfSequenceTest : AbstractParseTest() {
    @Test
    fun `discards the result of first sequence`() {
        val parser = prefixed(
            sequence(oneOf('a', 'b'), oneOf('1', '2')) { _, _ -> fail() },
            literal("!", 1)
        )

        parser.expecting {
            expectSequence {
                expectSequence {
                    expectOneOf('a', 'b', hasResult = false)
                    expectOneOf('1', '2', hasResult = false)
                }
                expectLiteral("!", result = 1)
            }
        }

        parser.matches("a2!", expected = 1)
        parser.matches("b1!", expected = 1)

        parser.doesNotMatch("a!") {
            failAt(1)
            expectLiteral("1")
            expectLiteral("2")
        }
    }
}