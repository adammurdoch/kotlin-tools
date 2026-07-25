package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.text.one
import kotlin.test.Test

class DiscardOfOneCharTest : AbstractParseTest() {
    @Test
    fun `discards the result of one char`() {
        val parser = discard(one())

        parser.expecting {
            expectOneChar(hasResult = false)
        }

        parser.matches("a")
        parser.matches("b")

        parser.doesNotMatch("") {
            expectOneChar()
        }
    }
}