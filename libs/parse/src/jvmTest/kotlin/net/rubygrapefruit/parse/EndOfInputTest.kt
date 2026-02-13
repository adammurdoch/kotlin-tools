package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.general.endOfInput
import kotlin.test.Test

class EndOfInputTest : AbstractParseTest() {
    @Test
    fun `matches end of input`() {
        val parser = endOfInput()

        parser.expecting {
            expectEndOfInput()
        }

        parser.matches {
            steps {
                commit(0)
            }
        }

        parser.doesNotMatch(0x1) {
            expectEndOfInput()
        }
    }

    @Test
    fun `matches end of input and produces a result`() {
        val parser = endOfInput(2)

        parser.expecting {
            expectEndOfInput(result = 2)
        }

        parser.matches(expected = 2)

        parser.doesNotMatch(0x1) {
            expectEndOfInput()
        }
    }
}