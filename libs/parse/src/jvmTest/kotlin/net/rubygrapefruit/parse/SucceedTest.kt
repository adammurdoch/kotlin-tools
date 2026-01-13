package net.rubygrapefruit.parse

import kotlin.test.Test

class SucceedTest : AbstractParseTest() {
    @Test
    fun `matches nothing`() {
        val parser = succeed()

        parser.expecting {
            emptyMatch()
        }

        parser.matches("")
        parser.matches()

        parser.doesNotMatch("X") {
            expectEndOfInput()
        }
        parser.doesNotMatch(0x4) {
            expectEndOfInput()
        }
    }

    @Test
    fun `matches nothing and produces result`() {
        val parser = succeed(1)

        parser.matches("", expected = 1)
        parser.matches(expected = 1)
    }
}