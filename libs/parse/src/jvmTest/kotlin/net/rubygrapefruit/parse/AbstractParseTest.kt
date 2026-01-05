package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.ByteInput
import net.rubygrapefruit.parse.byte.parse
import net.rubygrapefruit.parse.char.CharInput
import net.rubygrapefruit.parse.char.parse
import kotlin.test.assertIs

abstract class AbstractParseTest {
    fun matches(parser: Parser<CharInput, Unit>, input: String, config: CharParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        assertIs<ParseResult.Success<Unit>>(result)
    }

    fun doesNotMatch(parser: Parser<CharInput, Unit>, input: String, config: CharParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        assertIs<ParseResult.Fail>(result)
    }

    fun matches(parser: Parser<ByteInput, Unit>, vararg input: Byte, config: ByteParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        assertIs<ParseResult.Success<Unit>>(result)
    }

    fun doesNotMatch(parser: Parser<ByteInput, Unit>, vararg input: Byte, config: ByteParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        assertIs<ParseResult.Fail>(result)
    }

    interface CharParseFixture

    interface ByteParseFixture
}