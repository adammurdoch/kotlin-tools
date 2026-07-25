package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class Sequence3OfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches sequence of zero or more of one of char`() {
        val parser = sequence(
            zeroOrMore(oneOf('a', 'b')),
            zeroOrMore(oneOf('1', '2')),
            zeroOrMore(oneOf('!', '?'))
        ) { a, b, c -> a + b + c }

        parser.expecting {
            expectSequence {
                expectZeroOrMoreSingleInput {
                    expectOneOf("a", "b")
                }
                expectSequence {
                    expectZeroOrMoreSingleInput {
                        expectOneOf("1", "2")
                    }
                    expectZeroOrMoreSingleInput {
                        expectOneOf("!", "?")
                    }
                }
            }
        }

        parser.matches("", expected = listOf())
        parser.matches("a", expected = listOf('a'))
        parser.matches("1", expected = listOf('1'))
        parser.matches("!", expected = listOf('!'))
        parser.matches("bba211!?", expected = listOf('b', 'b', 'a', '2', '1', '1', '!', '?'))

        parser.doesNotMatch("X") {
            expectLiteral("a")
            expectLiteral("b")
            expectLiteral("1")
            expectLiteral("2")
            expectLiteral("!")
            expectLiteral("?")
            expectEndOfInput()
        }
    }
}