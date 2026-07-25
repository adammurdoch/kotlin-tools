package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.text.oneInRange
import kotlin.test.Test

class OneOfCharRangeTest : AbstractParseTest() {
    @Test
    fun `matches one of a range of chars`() {
        val parser = oneInRange('a'..'c')

        parser.expecting {
            expectOneInRange('a', 'c')
        }

        parser.matches("a", expected = 'a')
        parser.matches("b", expected = 'b')
        parser.matches("c", expected = 'c')

        // missing
        parser.doesNotMatch("") {
            expect("\"a\"..\"c\"")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expect("\"a\"..\"c\"")
        }

        // mismatched case
        parser.doesNotMatch("A") {
            expect("\"a\"..\"c\"")
        }

        // extra
        parser.doesNotMatch("cX") {
            failAt(1)
            expectEndOfInput()
        }
        parser.doesNotMatch("cb") {
            failAt(1)
            expectEndOfInput()
        }
    }
}