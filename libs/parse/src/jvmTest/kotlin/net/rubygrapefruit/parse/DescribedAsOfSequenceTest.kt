package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.describedAs
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DescribedAsOfSequenceTest : AbstractParseTest() {
    @Test
    fun `replaces expectation at start of sequence of char literals`() {
        val parser = describedAs(
            sequence(literal("a"), literal("b")),
            "<literal>"
        )

        parser.expecting {
            expectDescribed("<literal>") {
                expectSequence {
                    expectLiteral("a")
                    expectLiteral("b")
                }
            }
        }

        parser.matches("ab") {
            steps {
                commit(1)
                commit(1)
            }
        }

        // missing
        parser.doesNotMatch("") {
            expect("<literal>")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expect("<literal>")
        }
        parser.doesNotMatch("aX") {
            failAt(1)
            expectLiteral("b")
        }

        // extra
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
    }
}