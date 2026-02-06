package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.map
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.match
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test
import kotlin.test.fail

class ChoiceOfSequenceTest : AbstractParseTest() {
    @Test
    fun `matches choice of sequences with common prefix`() {
        val parser = oneOf(
            sequence(literal("ab", 1), literal("c", 2)) { a, b -> listOf(a, b) },
            sequence(literal("a", 5), literal("b", 6)) { a, b -> listOf(a, b) }
        )

        parser.expecting {
            expectChoice {
                expectSequence {
                    expectLiteral("ab", result = 1)
                    expectLiteral("c", result = 2)
                }
                expectSequence {
                    expectLiteral("a", result = 5)
                    expectLiteral("b", result = 6)
                }
            }
        }

        parser.matches("abc", expected = listOf(1, 2))
        parser.matches("ab", expected = listOf(5, 6))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
            expectLiteral("ab")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expectLiteral("c")
            expectEndOfInput()
        }
    }

    @Test
    fun `matches choice of sequences of map with common prefix then literal`() {
        val parser = oneOf(
            sequence(map(literal("a", 1)) { it.toString() }, literal("b", "b")) { a, b -> a + b },
            sequence(map(literal("a", 2)) { it.toString() }, literal("c", "c")) { a, b -> a + b },
        )

        parser.expecting {
            expectChoice {
                expectSequence {
                    expectMap {
                        expectLiteral("a", result = 1)
                    }
                    expectLiteral("b", result = "b")
                }
                expectSequence {
                    expectMap {
                        expectLiteral("a", result = 2)
                    }
                    expectLiteral("c", result = "c")
                }
            }
        }

        parser.matches("ab", expected = "1b")
        parser.matches("ac", expected = "2c")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
            expectLiteral("c")
        }

        // extra
        parser.doesNotMatch("acX") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches choice of sequences of char match with common prefix then literal`() {
        val parser = oneOf(
            sequence(match(literal("a")), literal("b", "b")) { a, b -> a + b },
            sequence(match(literal("a")), literal("c", "c")) { a, b -> a + b },
        )

        parser.expecting {
            expectChoice {
                expectSequence {
                    expectMatch { expectLiteral("a") }
                    expectLiteral("b", result = "b")
                }
                expectSequence {
                    expectMatch { expectLiteral("a") }
                    expectLiteral("c", result = "c")
                }
            }
        }

        parser.matches("ab", expected = "ab")
        parser.matches("ac", expected = "ac")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("b")
            expectLiteral("c")
        }

        // extra
        parser.doesNotMatch("acX") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches choice of sequences of zero or more of char with common prefix then literal`() {
        val parser = oneOf(
            sequence(zeroOrMore(oneOf('1', '2')), literal("b", 'b')) { a, b -> a + b },
            sequence(zeroOrMore(oneOf('1', '2')), literal("c", 'c')) { a, b -> a + b },
        )

        parser.expecting {
            expectChoice {
                expectSequence {
                    expectZeroOrMoreSingleInput {
                        expectOneOf("1", "2")
                    }
                    expectLiteral("b", result = 'b')
                }
                expectSequence {
                    expectZeroOrMoreSingleInput {
                        expectOneOf("1", "2")
                    }
                    expectLiteral("c", result = 'c')
                }
            }
        }

        parser.matches("b", expected = listOf('b'))
        parser.matches("c", expected = listOf('c'))

        parser.matches("2b", expected = listOf('2', 'b'))
        parser.matches("1c", expected = listOf('1', 'c'))

        parser.matches("221b", expected = listOf('2', '2', '1', 'b'))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("1")
            expectLiteral("2")
            expectLiteral("b")
            expectLiteral("c")
        }
        parser.doesNotMatch("1122") {
            failAt(4)
            expectLiteral("1")
            expectLiteral("2")
            expectLiteral("b")
            expectLiteral("c")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("1")
            expectLiteral("2")
            expectLiteral("b")
            expectLiteral("c")
        }
        parser.doesNotMatch("2211X") {
            failAt(4)
            expectLiteral("1")
            expectLiteral("2")
            expectLiteral("b")
            expectLiteral("c")
        }

        // extra
        parser.doesNotMatch("cX") {
            failAt(1)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches choice of sequences of zero or more of literal with common prefix then literal`() {
        val parser = oneOf(
            sequence(zeroOrMore(literal("12", '1')), literal("b", 'b')) { a, b -> a + b },
            sequence(zeroOrMore(literal("12", '2')), literal("c", 'c')) { a, b -> a + b },
        )

        parser.expecting {
            expectChoice {
                expectSequence {
                    expectChoice {
                        expectOneOrMore {
                            expectLiteral("12", result = '1')
                        }
                        expectZero()
                    }
                    expectLiteral("b", result = 'b')
                }
                expectSequence {
                    expectChoice {
                        expectOneOrMore {
                            expectLiteral("12", result = '2')
                        }
                        expectZero()
                    }
                    expectLiteral("c", result = 'c')
                }
            }
        }

        parser.matches("b", expected = listOf('b'))
        parser.matches("c", expected = listOf('c'))
        parser.matches("12b", expected = listOf('1', 'b'))
        parser.matches("12c", expected = listOf('2', 'c'))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("12")
            expectLiteral("b")
            expectLiteral("c")
        }
        parser.doesNotMatch("1212") {
            failAt(4)
            expectLiteral("12")
            expectLiteral("b")
            expectLiteral("c")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("12")
            expectLiteral("b")
            expectLiteral("c")
        }
        parser.doesNotMatch("12X") {
            failAt(2)
            expectLiteral("12")
            expectLiteral("b")
            expectLiteral("c")
        }

        // extra
        parser.doesNotMatch("12cX") {
            failAt(3)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches choice of sequences of choice with common prefix then literal`() {
        val parser = oneOf(
            sequence(
                oneOf(
                    literal("a", "A"),
                    literal("b", "B")
                ),
                literal("!", "!")
            ) { a, b -> a + b },
            sequence(
                oneOf(
                    literal("a", "a"),
                    literal("b", "b")
                ),
                literal("?", "?")
            ) { a, b -> a + b },
        )

        parser.expecting {
            expectChoice {
                expectSequence {
                    expectChoice {
                        expectLiteral("a", result = "A")
                        expectLiteral("b", result = "B")
                    }
                    expectLiteral("!", result = "!")
                }
                expectSequence {
                    expectChoice {
                        expectLiteral("a", result = "a")
                        expectLiteral("b", result = "b")
                    }
                    expectLiteral("?", result = "?")
                }
            }
        }

        parser.matches("b!", expected = "B!")
        parser.matches("a?", expected = "a?")

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral("!")
            expectLiteral("?")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("bX") {
            failAt(1)
            expectLiteral("!")
            expectLiteral("?")
        }

        // extra
        parser.doesNotMatch("b?X") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches choice of sequence of sequence with common prefix then literal`() {
        val parser = oneOf(
            sequence(sequence(literal("a"), literal("b")), literal("?")),
            sequence(sequence(literal("a"), literal("b")), literal("!"))
        )

        parser.expecting {
            expectChoice {
                expectSequence {
                    expectSequence {
                        expectLiteral("a")
                        expectLiteral("b")
                    }
                    expectLiteral("?")
                }
                expectSequence {
                    expectSequence {
                        expectLiteral("a")
                        expectLiteral("b")
                    }
                    expectLiteral("!")
                }
            }
        }

        parser.matches("ab?")
        parser.matches("ab!")

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
            expectLiteral("?")
            expectLiteral("!")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
        }
        parser.doesNotMatch("abX") {
            failAt(2)
            expectLiteral("?")
            expectLiteral("!")
        }
    }

    @Test
    fun `matches choice of sequence or literal with common prefix`() {
        val parser = oneOf(
            sequence(
                literal("a", 1),
                zeroOrMore(literal("1", 1)),
                literal("!", 1)
            ) { a, b, c -> listOf(a) + b + listOf(c) },
            literal("a", listOf(2))
        )

        parser.expecting {
            expectChoice {
                expectSequence {
                    expectLiteral("a", result = 1)
                    expectSequence {
                        expectChoice {
                            expectOneOrMore {
                                expectLiteral("1", result = 1)
                            }
                            expectZero()
                        }
                        expectLiteral("!", result =  1)
                    }
                }
                expectLiteral("a", result =  listOf(2))
            }
        }

        parser.matches("a!", expected = listOf(1, 1))
        parser.matches("a11!", expected = listOf(1, 1, 1, 1))
        parser.matches("a", expected = listOf(2))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
        }
        parser.doesNotMatch("a1X") {
            failAt(2)
            expectLiteral("!")
            expectLiteral("1")
        }

        // extra
        parser.doesNotMatch("aX") {
            failAt(1)
            expectLiteral("!")
            expectLiteral("1")
            expectEndOfInput()
        }
        parser.doesNotMatch("a!X") {
            failAt(2)
            expectEndOfInput()
        }
    }

    @Test
    fun `does not call map function of discarded option`() {
        val parser = oneOf(
            sequence(literal("ab", 1), literal("c", 2)) { a, b -> listOf(a, b) },
            sequence(literal("a", 5), literal("b", 6)) { _, _ -> fail() }
        )

        parser.matches("abc", expected = listOf(1, 2))
    }
}