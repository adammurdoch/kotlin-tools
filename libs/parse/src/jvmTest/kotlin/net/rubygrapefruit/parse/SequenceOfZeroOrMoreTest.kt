package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class SequenceOfZeroOrMoreTest : AbstractParseTest() {
    @Test
    fun `matches zero or more literals then zero or more literals with common prefix`() {
        val parser = sequence(
            zeroOrMore(literal("abc", 1)),
            zeroOrMore(literal("ad", 2))
        ) { a, b -> a + b }

        parser.expecting {
            expectSequence {
                expectChoice {
                    expectOneOrMore {
                        expectLiteral("abc", result = 1)
                    }
                    expectZero()
                }
                expectChoice {
                    expectOneOrMore {
                        expectLiteral("ad", result = 2)
                    }
                    expectZero()
                }
            }
        }

        parser.matches("", expected = listOf())
        parser.matches("ad", expected = listOf(2))
        parser.matches("adad", expected = listOf(2, 2))
        parser.matches("abc", expected = listOf(1))
        parser.matches("abcabc", expected = listOf(1, 1))
        parser.matches("abcad", expected = listOf(1, 2))
        parser.matches("abcabcad", expected = listOf(1, 1, 2))
        parser.matches("abcabcadad", expected = listOf(1, 1, 2, 2))

        // missing
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectLiteral("ad")
            expectEndOfInput()
        }
        parser.doesNotMatch("ab") {
            expectLiteral("abc")
            expectLiteral("ad")
            expectEndOfInput()
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectLiteral("ad")
            expectEndOfInput()
        }
        parser.doesNotMatch("aX") {
            expectLiteral("abc")
            expectLiteral("ad")
            expectEndOfInput()
        }
        parser.doesNotMatch("abX") {
            expectLiteral("abc")
            expectLiteral("ad")
            expectEndOfInput()
        }

        // extra
        parser.doesNotMatch("adX") {
            failAt(2)
            expectLiteral("ad")
            expectEndOfInput()
        }
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectLiteral("abc")
            expectLiteral("ad")
            expectEndOfInput()
        }
    }

    @Test
    fun `matches zero or more literals then literal with common prefix`() {
        val parser = sequence(
            zeroOrMore(literal("abc", 1)),
            literal("ad", 2)
        ) { a, b -> a + b }

        parser.expecting {
            expectSequence {
                expectChoice {
                    expectOneOrMore {
                        expectLiteral("abc", result = 1)
                    }
                    expectZero()
                }
                expectLiteral("ad", result = 2)
            }
        }

        parser.matches("ad", expected = listOf(2))
        parser.matches("abcad", expected = listOf(1, 2))
        parser.matches("abcabcad", expected = listOf(1, 1, 2))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("a") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("ab") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("abca") {
            failAt(3)
            expectLiteral("abc")
            expectLiteral("ad")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("aX") {
            expectLiteral("abc")
            expectLiteral("ad")
        }
        parser.doesNotMatch("abcaX") {
            failAt(3)
            expectLiteral("abc")
            expectLiteral("ad")
        }

        // extra
        parser.doesNotMatch("adX") {
            failAt(2)
            expectEndOfInput()
        }
        parser.doesNotMatch("abcadX") {
            failAt(5)
            expectEndOfInput()
        }
    }

    @Test
    fun `matches zero or more one of char then literal`() {
        val parser = sequence(
            zeroOrMore(oneOf('a', 'b')),
            literal(".end", listOf('.'))
        ) { a, b -> a + b }

        parser.expecting {
            expectSequence {
                expectZeroOrMoreSingleInput("a", "b")
                expectLiteral(".end", result = listOf('.'))
            }
        }

        parser.matches(".end", expected = listOf('.'))
        parser.matches("a.end", expected = listOf('a', '.'))
        parser.matches("baa.end", expected = listOf('b', 'a', 'a', '.'))

        // missing
        parser.doesNotMatch("") {
            expectLiteral(".end")
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectLiteral(".end")
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("b.") {
            failAt(1)
            expectLiteral(".end")
            expectLiteral("a")
            expectLiteral("b")
        }
        parser.doesNotMatch("b.en?") {
            failAt(1)
            expectLiteral(".end")
            expectLiteral("a")
            expectLiteral("b")
        }
    }

    @Test
    fun `matches zero or more one of char then choice of literal`() {
        val parser = sequence(
            zeroOrMore(oneOf('a', 'b')),
            oneOf(
                literal("?", '?'),
                literal("!", '!')
            )
        ) { a, b -> a + b }

        parser.expecting {
            expectSequence {
                expectZeroOrMoreSingleInput("a", "b")
                expectChoice {
                    expectLiteral("?", result = '?')
                    expectLiteral("!", result = '!')
                }
            }
        }

        parser.matches("?", expected = listOf('?'))
        parser.matches("ba!", expected = listOf('b', 'a', '!'))

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a")
            expectLiteral("b")
            expectLiteral("?")
            expectLiteral("!")
        }
        parser.doesNotMatch("bb") {
            failAt(2)
            expectLiteral("a")
            expectLiteral("b")
            expectLiteral("?")
            expectLiteral("!")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a")
            expectLiteral("b")
            expectLiteral("?")
            expectLiteral("!")
        }

        // extra
        parser.doesNotMatch("?X") {
            failAt(1)
            expectEndOfInput()
        }
        parser.doesNotMatch("ab!X") {
            failAt(3)
            expectEndOfInput()
        }
    }
}