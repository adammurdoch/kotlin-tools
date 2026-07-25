package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.literal
import kotlin.test.Test

class RepeatTest : AbstractParseTest() {
    @Test
    fun `parses n text literals`() {
        val parser = repeat(3, literal("a.", 1))

        parser.expecting {
            expectRepeat(3) {
                expectLiteral("a.", result = 1)
            }
        }

        parser.matches("a.a.a.", listOf(1, 1, 1)) {
            steps {
                advance(2)
                advance(2)
                advance(2)
            }
        }

        // missing
        parser.doesNotMatch("") {
            expectLiteral("a.")
            steps {}
        }
        parser.doesNotMatch("a.a.") {
            failAt(4)
            expectLiteral("a.")
            steps {
                advance(2)
                advance(2)
            }
        }
        parser.doesNotMatch("a.a") {
            failAt(2)
            expectLiteral("a.")
            steps {
                advance(2)
            }
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectLiteral("a.")
            steps { }
        }
        parser.doesNotMatch("a.Xa") {
            failAt(2)
            expectLiteral("a.")
            steps {
                advance(2)
            }
        }

        // extra
        parser.doesNotMatch("a.a.a.a") {
            failAt(6)
            expectEndOfInput()
            steps {
                advance(2)
                advance(2)
                advance(2)
            }
        }
        parser.doesNotMatch("a.a.a.XX") {
            failAt(6)
            expectEndOfInput()
            steps {
                advance(2)
                advance(2)
                advance(2)
            }
        }
    }

    @Test
    fun `can match zero instances`() {
        val parser = repeat(0, literal("abc", 2))

        parser.expecting {
            expectSucceed(result = emptyList<Int>())
        }

        parser.matches("", expected = listOf()) {
            steps {
                advance(0) // uses success parser
            }
        }

        // unexpected
        parser.doesNotMatch("X") {
            expectEndOfInput()
        }

        // extra
        parser.doesNotMatch("abc") {
            expectEndOfInput()
        }
    }
}