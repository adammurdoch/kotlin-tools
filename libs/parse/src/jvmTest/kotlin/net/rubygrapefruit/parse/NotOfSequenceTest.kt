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

        parser.matches("") {
            steps {
                commit(0)
            }
        }

        // matches predicate
        parser.doesNotMatch("a1") {
            expect("not \"a\"")
            expectEndOfInput()
            steps { }
        }

        // does not match predicate
        parser.doesNotMatch("X") {
            expect("not \"a\"")
            expectEndOfInput()
            steps {
                commit(0)
            }
        }
        parser.doesNotMatch("aX") {
            expect("not \"a\"")
            expectEndOfInput()
            steps { }
        }
    }
}