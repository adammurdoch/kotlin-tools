package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.ByteStream
import net.rubygrapefruit.parse.byte.parse
import net.rubygrapefruit.parse.char.CharStream
import net.rubygrapefruit.parse.char.parse
import kotlin.test.assertIs

abstract class AbstractParseTest {
    fun matches(parser: Parser<CharStream, Unit>, input: String, config: CharParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        assertIs<ParseResult.Success<Unit>>(result)
    }

    fun doesNotMatch(parser: Parser<CharStream, Unit>, input: String, config: CharParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        assertIs<ParseResult.Fail>(result)
    }

    fun matches(parser: Parser<ByteStream, Unit>, vararg input: Byte, config: ByteParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        assertIs<ParseResult.Success<Unit>>(result)
    }

    fun doesNotMatch(parser: Parser<ByteStream, Unit>, vararg input: Byte, config: ByteParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        assertIs<ParseResult.Fail>(result)
    }

    interface CharParseFixture

    interface ByteParseFixture
}