package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.AbstractParseTest
import kotlin.test.Test

class EndOfLineTest : AbstractParseTest() {
    @Test
    fun `matches line feed or carriage return line feed`() {
        val parser = endOfLine()

        parser.matches("\n")
        parser.matches("\r\n")

        // missing
        parser.doesNotMatch("") {
            expect("end of line")
        }
        parser.doesNotMatch("\r") {
            expect("end of line")
        }

        // unexpected
        parser.doesNotMatch("X") {
            expect("end of line")
        }
        parser.doesNotMatch("\rX") {
            expect("end of line")
        }

        // extra
        parser.doesNotMatch("\nX") {
            failAt(1, 2, 1)
            expectContext("", "X")
            expectEndOfInput()
        }
        parser.doesNotMatch("\r\nX") {
            failAt(2, 2, 1)
            expectContext("", "X")
            expectEndOfInput()
        }
        parser.doesNotMatch("\n\n") {
            failAt(1, 2, 1)
            expectContext("", "")
            expectEndOfInput()
        }
        parser.doesNotMatch("\n\r") {
            failAt(1, 2, 1)
            expectContext("", "\r")
            expectEndOfInput()
        }
    }
}
