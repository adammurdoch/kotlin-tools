package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.literal
import kotlin.test.Test

class NotParserTest : AbstractParseTest() {
    @Test
    fun `matches empty input only`() {
        val parser = not(literal(byteArrayOf(0x1)))

        parser.matches()

        parser.doesNotMatch(0x1) {
//            expectEndOfInput()
        }
        parser.doesNotMatch(0x1, 0x2) {
//            expectEndOfInput()
        }
        parser.doesNotMatch(0x2) {
            expectEndOfInput()
        }
    }
}