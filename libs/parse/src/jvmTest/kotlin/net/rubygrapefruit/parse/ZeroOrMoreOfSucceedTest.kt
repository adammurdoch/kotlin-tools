package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.general.succeed
import kotlin.test.Test

class ZeroOrMoreOfSucceedTest : AbstractParseTest() {
    @Test
    fun `matches nothing`() {
        val parser = zeroOrMore(succeed(2))

        parser.expecting {
            emptyMatch()
            expectIsChoice(2) // could be replaced with succeed()
        }

        parser.matches("", expected = listOf(2))

        parser.doesNotMatch("x") {
            expectEndOfInput()
        }
    }
}