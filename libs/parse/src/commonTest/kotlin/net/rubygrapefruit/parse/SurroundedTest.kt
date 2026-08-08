package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.surrounded
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class SurroundedTest : AbstractParseTest() {
    @Test
    fun `matches literal then literal then literal`() {
        val parser = surrounded(
            literal("a", 1),
            literal("b", 2),
            literal("c", 3)
        )

        parser.expecting {
            expectSequence {
                expectLiteral("a")
                expectSequence {
                    expectLiteral("b", result = 2)
                    expectLiteral("c")
                }
            }
        }

        parser.matches("abc", expected = 2)

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
        }
        parser.doesNotMatch("ab") {
            failAt(2)
            expectLiteral("c")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expectLiteral("c")
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
    }

    @Test
    fun `can map value`() {
        val parser = surrounded(literal("a", 1), literal("b", 2), literal("c", 3)) { "[$it]" }

        parser.expecting {
            expectSequence {
                expectLiteral("a")
                expectSequence {
                    expectLiteral("b", result = 2)
                    expectLiteral("c")
                }
            }
        }

        parser.matches("abc", expected = "[2]")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
        }
        parser.doesNotMatch("ab") {
            failAt(2)
            expectLiteral("c")
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
    }

}