package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.general.succeed
import kotlin.test.Test

class ZeroOrMoreOfSucceedTest : AbstractParseTest() {
    @Test
    fun `matches nothing`() {
        val parser = zeroOrMore(succeed(2))

        parser.expecting {
            // could be replaced with succeed()
            expectChoice {
                expectOneOrMore {
                    expectSucceed(result = 2)
                }
                expectZero()
            }
        }

        parser.matches("", expected = listOf(2))

        parser.doesNotMatch("x") {
            expectEndOfInput()
        }
    }
}