package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.recursive
import net.rubygrapefruit.parse.text.TextInput
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.parse
import kotlin.test.Test

class RecursiveTest : AbstractParseTest() {
    @Test
    fun `must define parser`() {
        val parser = recursive<TextInput, String>()

        failsWith<IllegalStateException> {
            parser.parse("??")
        }
    }

    @Test
    fun `cannot define parser more than once`() {
        val parser = recursive<TextInput, String>()

        parser.parser(literal("abc", "1"))
        failsWith<IllegalStateException> {
            parser.parser(literal("123", "2"))
        }
    }
}