package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.text.match
import net.rubygrapefruit.parse.text.one
import kotlin.test.Test

class MatchOfRepeatTest : AbstractParseTest() {
    @Test
    fun `matches repeat of char`() {
        val parser = match(repeat(3, one()))

        parser.expecting {
            expectMatch {
                expectRepeat(3, hasResult = false) {
                    expectOneChar(hasResult = false)
                }
            }
        }

        parser.matches("abc", expected = "abc")
        parser.matches("123", expected = "123")

        // missing
        parser.doesNotMatch("") {
            expectOneChar()
        }
        parser.doesNotMatch("a") {
            failAt(1)
            expectOneChar()
        }
        parser.doesNotMatch("ab") {
            failAt(2)
            expectOneChar()
        }

        // extra
        parser.doesNotMatch("123X") {
            failAt(3)
            expectEndOfInput()
        }
    }
}