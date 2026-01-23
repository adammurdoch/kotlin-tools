package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.ByteInput
import net.rubygrapefruit.parse.general.endOfInput
import kotlin.test.Test

class EndOfInputParserTest : AbstractParseTest() {
    @Test
    fun `matches end of input`() {
        val parser = endOfInput<ByteInput>()

        parser.matches()

        parser.doesNotMatch(0x1) {
            expectEndOfInput()
        }
    }
}