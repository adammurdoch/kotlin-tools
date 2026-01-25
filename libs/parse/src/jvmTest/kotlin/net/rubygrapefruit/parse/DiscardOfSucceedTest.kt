package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.discard
import net.rubygrapefruit.parse.general.succeed
import kotlin.test.Test

class DiscardOfSucceedTest : AbstractParseTest() {
    @Test
    fun `discards result of succeed`() {
        val parser = discard(succeed(12))

        parser.expecting {
            expectSucceed(result = Unit)
        }

        parser.matches("")

        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
    }

    @Test
    fun `discards result of succeed that does not produce a value`() {
        val parser = discard(succeed())

        parser.expecting {
            expectSucceed(result = Unit)
        }

        parser.matches("")

        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
    }
}