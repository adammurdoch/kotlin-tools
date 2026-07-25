package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.general.endOfInput
import net.rubygrapefruit.parse.text.match
import kotlin.test.Test

class MatchOfEndOfInputTest : AbstractParseTest() {
    @Test
    fun `matches end of text`() {
        val parser = match(endOfInput(5))

        parser.expecting {
            expectMatch {
                expectEndOfInput()
            }
        }

        parser.matches("", expected = "")
    }
}