package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AbstractParseTest
import kotlin.test.Test

class IntegerTest : AbstractParseTest() {
    @Test
    fun `matches positive integer`() {
        val parser = integer()

        parser.matches("0", expected = 0)
        parser.matches("9", expected = 9)
        parser.matches("112", expected = 112)
        parser.matches("9001", expected = 9001)

        // leading zero
        parser.doesNotMatch("01") {
            failAt(1)
            expectEndOfInput()
        }
        parser.doesNotMatch("001") {
            failAt(1)
            expectEndOfInput()
        }

        // negative
        parser.doesNotMatch("-1") {
            expect("an integer")
        }

        // extra
        parser.doesNotMatch("12X") {
            failAt(2)
            expectOneInRange('0', '9')
            expectEndOfInput()
        }
    }
}