package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.one
import kotlin.test.Test

class OneCharTest : AbstractParseTest() {
    @Test
    fun `matches one character`() {
        val parser = one()

        parser.expecting {
            expectOneChar()
        }

        parser.matches("a", expected = 'a')
        parser.matches("b", expected = 'b')

        // missing
        parser.doesNotMatch("") {
            expect("one character")
        }

        // extra
        parser.doesNotMatch("aX") {
            failAt(1)
            expectEndOfInput()
        }
    }
}