package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.oneOf
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.oneOf
import kotlin.test.Test

class ZeroOrMoreOfOneOfSetTest : AbstractParseTest() {
    @Test
    fun `matches zero or more of one of char`() {
        val parser = zeroOrMore(
            oneOf('a', 'b')
        )

        parser.expecting {
            expectZeroOrMoreSingleInput {
                expectOneOf("a", "b")
            }
        }

        parser.matches("", expected = emptyList()) {
            steps {
                commit(0)
            }
        }
        parser.matches("a", expected = listOf('a')) {
            steps {
                commit(1)
            }
        }
        parser.matches("b", expected = listOf('b'))
        parser.matches("baa", expected = listOf('b', 'a', 'a')) {
            steps {
                commit(3)
            }
        }

        // unexpected
        parser.doesNotMatch("1") {
            expectLiteral("a")
            expectLiteral("b")
            expectEndOfInput()
        }
        parser.doesNotMatch("ba1") {
            failAt(2)
            expectLiteral("a")
            expectLiteral("b")
            expectEndOfInput()
        }
    }

    @Test
    fun `matches zero or more of one of byte`() {
        val parser = zeroOrMore(
            oneOf(0x1, 0x2)
        )

        parser.expecting {
            expectZeroOrMoreSingleInput {
                expectOneOf(0x1, 0x2)
            }
        }

        parser.matches(expected = emptyList()) {
            steps {
                commit(0)
            }
        }
        parser.matches(0x1, expected = bytes(0x1)) {
            steps {
                commit(1)
            }
        }
        parser.matches(0x2, expected = bytes(0x2))
        parser.matches(0x2, 0x1, 0x1, expected = bytes(0x2, 0x1, 0x1)) {
            steps {
                commit(3)
            }
        }

        // unexpected
        parser.doesNotMatch(0x3) {
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
        parser.doesNotMatch(0x2, 0x1, 0x3) {
            failAt(2)
            expectEndOfInput()
            expectLiteral(0x1)
            expectLiteral(0x2)
        }
    }
}