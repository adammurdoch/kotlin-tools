package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.repeat
import net.rubygrapefruit.parse.general.succeed
import kotlin.test.Test

class RepeatOfSuccessTest : AbstractParseTest() {
    @Test
    fun `matches nothing`() {
        val parser = repeat(3, succeed(1))

        parser.expecting {
            expectRepeat(3) {
                expectSucceed(result = 1)
            }
        }

        parser.matches(expected = listOf(1, 1, 1))

        parser.doesNotMatch(0x1) {
            expectEndOfInput()
        }
        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
    }
}