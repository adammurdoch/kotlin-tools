package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class DiscardAllSequenceTest : AbstractParseTest() {
    @Test
    fun `matches 2 literals`() {
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
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
        }

        // extra
        parser.doesNotMatch("abX") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches 3 literal`() {
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

    @Test
    fun `matches 4 literals`() {
        val parser = sequence(literal("a"), literal("b"), literal("c"), literal("d"))

        parser.expecting {
            expectSequence {
                expectLiteral("a")
                expectSequence {
                    expectLiteral("b")
                    expectSequence {
                        expectLiteral("c")
                        expectLiteral("d")
                    }
                }
            }
        }

        parser.matches("abcd")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("abc") {
            failAt(3)
            expectLiteral("d")
        }

        // extra
        parser.doesNotMatch("abcdX") {
            failAt(4)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches 6 literals`() {
        val parser = sequence(literal("a"), literal("b"), literal("c"), literal("d"), literal("e"), literal("f"))

        parser.expecting {
            expectSequence {
                expectLiteral("a")
                expectSequence {
                    expectLiteral("b")
                    expectSequence {
                        expectLiteral("c")
                        expectSequence {
                            expectLiteral("d")
                            expectSequence {
                                expectLiteral("e")
                                expectLiteral("f")
                            }
                        }
                    }
                }
            }
        }

        parser.matches("abcdef")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("abcde") {
            failAt(5)
            expectLiteral("f")
        }

        // extra
        parser.doesNotMatch("abcdefX") {
            failAt(6)
            expectEndOfInput()
        }
    }
}