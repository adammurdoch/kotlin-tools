package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.byte.ByteInput
import net.rubygrapefruit.parse.byte.BytePosition
import net.rubygrapefruit.parse.byte.parse
import net.rubygrapefruit.parse.byte.pushParser
import net.rubygrapefruit.parse.char.CharInput
import net.rubygrapefruit.parse.char.CharPosition
import net.rubygrapefruit.parse.char.parse
import net.rubygrapefruit.parse.char.pushParser
import kotlin.test.assertEquals
import kotlin.test.assertIs

abstract class AbstractParseTest {
    fun matches(parser: Parser<CharInput, Unit>, input: String) {
        matches(parser, input = input, expected = Unit)
    }

    fun <T> matches(parser: Parser<CharInput, T>, input: String, expected: T) {
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

    fun doesNotMatch(parser: Parser<CharInput, Unit>, input: String, config: CharParseFailureFixture.() -> Unit = {}) {
        val fixture = DefaultCharParseFailureFixture()
        fixture.config()

        val result = parser.parse(input)
        result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.message())
    }

    fun matches(parser: Parser<ByteInput, Unit>, vararg input: Byte) {
        matches(parser, input = input, expected = Unit)
    }

    fun <T> matches(parser: Parser<ByteInput, T>, vararg input: Byte, expected: T) {
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

    fun doesNotMatch(parser: Parser<ByteInput, Unit>, vararg input: Byte, config: ByteParseFailureFixture.() -> Unit = {}) {
        val fixture = DefaultByteParseFailureFixture()
        fixture.config()

        val result = parser.parse(input)
        result.assertIsFail(fixture.offset, fixture.message())
    }

    private fun <T> ParseResult<*, T>.assertIsSuccess(expected: T) {
        assertIs<ParseResult.Success<T>>(this)
        assertEquals(expected, value)
    }

    private fun ParseResult<CharPosition, *>.assertIsFail(offset: Int, line: Int, col: Int, message: String) {
        assertIs<ParseResult.Fail<CharPosition>>(this)
        assertEquals(offset, position.offset, "unexpected offset")
        assertEquals(line, position.line, "unexpected line")
        assertEquals(col, position.col, "unexpected column")
        assertEquals(message, this.message)
    }

    private fun ParseResult<BytePosition, *>.assertIsFail(offset: Int, message: String) {
        assertIs<ParseResult.Fail<BytePosition>>(this)
        assertEquals(offset, position.offset)
        assertEquals(message, this.message)
    }

    interface CharParseFailureFixture {
        fun failAt(offset: Int, line: Int, col: Int)

        fun expect(text: String)

        fun expectEndOfInput() {
            expect("end of input")
        }
    }

    private class DefaultCharParseFailureFixture : CharParseFailureFixture {
        var offset = 0
        var line = 1
        var col = 1
        val expect = mutableListOf<String>()

        override fun failAt(offset: Int, line: Int, col: Int) {
            this.offset = offset
            this.line = line
            this.col = col
        }

        override fun expect(text: String) {
            expect.add(text)
        }

        fun message(): String {
            return "expected ${expect.joinToString(", ")}"
        }
    }

    interface ByteParseFailureFixture {
        fun failAt(offset: Int)

        fun expect(text: String)

        fun expectEndOfInput() {
            expect("end of input")
        }
    }

    private class DefaultByteParseFailureFixture : ByteParseFailureFixture {
        var offset = 0
        val expect = mutableListOf<String>()

        override fun failAt(offset: Int) {
            this.offset = offset
        }

        override fun expect(text: String) {
            expect.add(text)
        }

        fun message(): String {
            return "expected ${expect.joinToString(", ")}"
        }
    }

}