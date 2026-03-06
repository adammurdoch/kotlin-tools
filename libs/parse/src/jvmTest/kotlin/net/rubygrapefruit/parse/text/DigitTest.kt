package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AbstractParseTest
import kotlin.test.Test

class DigitTest : AbstractParseTest() {
    @Test
    fun `matches digit char`() {
        val parser = digit()

        parser.matches("0", expected = '0')
        parser.matches("9", expected = '9')

        // missing
        parser.doesNotMatch("") {
            expect("a digit")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expect("a digit")
        }

        // extra
        parser.doesNotMatch("1X") {
            failAt(1)
            expectEndOfInput()
        }
    }
}