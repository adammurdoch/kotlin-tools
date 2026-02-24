package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.oneOrMore
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.general.succeed
import kotlin.test.Test

class OneOrMoreOfSucceedTest : AbstractParseTest() {
    @Test
    fun `matches nothing`() {
        val parser = oneOrMore(succeed(2))

        parser.expecting {
            // could be replaced with succeed()
            expectOneOrMore {
                expectSucceed(result = 2)
            }
        }

        parser.matches("", expected = listOf(2))

        parser.doesNotMatch("x") {
            expectEndOfInput()
        }
    }
}