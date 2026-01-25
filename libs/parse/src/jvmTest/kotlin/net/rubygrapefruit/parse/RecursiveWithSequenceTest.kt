package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.recursive
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.CharInput
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class RecursiveWithSequenceTest : AbstractParseTest() {
    @Test
    fun `matches recursive parser that is second parser in sequence`() {
        val parser = recursive<CharInput, Int>()
        val sequence = sequence(literal("."), zeroOrMore(parser)) { _, b -> 1 + b.sum() }
        parser.parser(sequence)

        parser.expecting {
            expectRecursive {
                expectSequence {
                    expectLiteral(".")
                    expectChoice {
                        expectOneOrMore {
                            expectRecurses()
                        }
                        expectZero()
                    }
                }
            }
        }

        parser.matches(".", expected = 1)
        parser.matches("...", expected = 3)

        parser.doesNotMatch("") {
            expectLiteral(".")
        }
        parser.doesNotMatch("X") {
            expectLiteral(".")
        }
        parser.doesNotMatch(".X") {
            failAt(1)
            expectLiteral(".")
            expectEndOfInput()
        }
    }
}