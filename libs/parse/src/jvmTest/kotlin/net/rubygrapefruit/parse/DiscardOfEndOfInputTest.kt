package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.general.endOfInput
import kotlin.test.Test

class DiscardOfEndOfInputTest : AbstractParseTest() {
    @Test
    fun `discards result of end of input`() {
        val parser = discard(endOfInput(12))

        parser.expecting {
            expectEndOfInput(result = Unit)
        }

        parser.matches()

        parser.doesNotMatch(0x13) {
            expectEndOfInput()
        }
    }

    @Test
    fun `does nothing for end of input that does not produce a value`() {
        val parser = discard(endOfInput())

        parser.expecting {
            expectEndOfInput(result = Unit)
        }

        parser.matches("")

        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
    }
}