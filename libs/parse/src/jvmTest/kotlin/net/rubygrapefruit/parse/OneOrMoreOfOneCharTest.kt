package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.text.one
import kotlin.test.Test

class OneOrMoreOfOneCharTest : AbstractParseTest() {
    /**
     * NOTE: this is in a separate class to the binar equivalent because importing both `one()` overloads in the same class is painful.
     */
    @Test
    fun `matches one or more of one char`() {
        val parser = oneOrMore(one())

        parser.expecting {
            expectOneOrMoreSingleInput {
                expectOneChar()
            }
        }

        parser.matches("a", expected = listOf('a')) {
            steps {
                advance(1)
            }
        }
        parser.matches("a12", expected = listOf('a', '1', '2')) {
            steps {
                advance(3)
            }
        }

        // missing
        parser.doesNotMatch("") {
            expectOneChar()
            steps {}
        }
    }
}