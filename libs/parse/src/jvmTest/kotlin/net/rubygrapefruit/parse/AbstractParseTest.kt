package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.ByteInput
import net.rubygrapefruit.parse.byte.parse
import net.rubygrapefruit.parse.byte.pushParser
import net.rubygrapefruit.parse.char.CharInput
import net.rubygrapefruit.parse.char.parse
import net.rubygrapefruit.parse.char.pushParser
import kotlin.test.assertEquals
import kotlin.test.assertIs

abstract class AbstractParseTest {
    fun matches(parser: Parser<CharInput, Unit>, input: String, config: CharParseFixture.() -> Unit = {}) {
        matches(parser, input = input, expected = Unit, config)
    }

    fun <T> matches(parser: Parser<CharInput, T>, input: String, expected: T, config: CharParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        result.assertIsSuccess(expected)

        val pushParser1 = parser.pushParser()
        pushParser1.input(input.toCharArray())
        val result1 = pushParser1.endOfInput()
        result1.assertIsSuccess(expected)

        val pushParser2 = parser.pushParser()
        for (index in input.indices) {
            pushParser2.input(charArrayOf(input[index]))
        }
        val result2 = pushParser2.endOfInput()
        result2.assertIsSuccess(expected)
    }

    fun doesNotMatch(parser: Parser<CharInput, Unit>, input: String, config: CharParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        result.assertIsFail()
    }

    fun matches(parser: Parser<ByteInput, Unit>, vararg input: Byte, config: ByteParseFixture.() -> Unit = {}) {
        matches(parser, input = input, expected = Unit, config)
    }

    fun <T> matches(parser: Parser<ByteInput, T>, vararg input: Byte, expected: T, config: ByteParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        result.assertIsSuccess(expected)

        val pushParser1 = parser.pushParser()
        pushParser1.input(input)
        val result1 = pushParser1.endOfInput()
        result1.assertIsSuccess(expected)

        val pushParser2 = parser.pushParser()
        for (index in input.indices) {
            pushParser2.input(byteArrayOf(input[index]))
        }
        val result2 = pushParser2.endOfInput()
        result2.assertIsSuccess(expected)
    }

    fun doesNotMatch(parser: Parser<ByteInput, Unit>, vararg input: Byte, config: ByteParseFixture.() -> Unit = {}) {
        val result = parser.parse(input)
        result.assertIsFail()
    }

    private fun <T> ParseResult<T>.assertIsSuccess(expected: T) {
        assertIs<ParseResult.Success<T>>(this)
        assertEquals(expected, value)
    }

    private fun ParseResult<*>.assertIsFail() {
        assertIs<ParseResult.Fail>(this)
    }

    interface CharParseFixture

    interface ByteParseFixture
}