package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.one
import kotlin.test.Test

class RepeatOfOneCharTest : AbstractParseTest() {
    /**
     * NOTE: this is in a separate class to the binary equivalent because importing both `one()` overloads in the same class is painful.
     */
    @Test
    fun `matches n of one char`() {
        val parser = repeat(3, one())

        parser.expecting {
            expectRepeatSingleInput(3) {
                expectOneChar()
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
        parser.doesNotMatch("ab") {
            failAt(2)
            expectOneChar()
            steps {}
        }

        // extra
        parser.doesNotMatch("abcX") {
            failAt(3)
            expectEndOfInput()
            steps {
                advance(3)
            }
        }
    }
}