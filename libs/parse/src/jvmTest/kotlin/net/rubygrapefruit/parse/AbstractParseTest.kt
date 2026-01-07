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
    fun Parser<CharInput, Unit>.matches(input: String) {
        matches(input = input, expected = Unit)
    }

    fun <T> Parser<CharInput, T>.matches(input: String, expected: T) {
        val result = parse(input)
        result.assertIsSuccess(expected)

        val pushParser1 = pushParser()
        pushParser1.input(input.toCharArray())
        val result1 = pushParser1.endOfInput()
        result1.assertIsSuccess(expected)

        val pushParser2 = pushParser()
        for (index in input.indices) {
            pushParser2.input(charArrayOf(input[index]))
        }
        val result2 = pushParser2.endOfInput()
        result2.assertIsSuccess(expected)
    }

    fun Parser<CharInput, *>.doesNotMatch(input: String, config: CharParseFailureFixture.() -> Unit = {}) {
        val fixture = DefaultCharParseFailureFixture()
        fixture.config()

        val result = parse(input)
        result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.message())
    }

    fun Parser<ByteInput, Unit>.matches(vararg input: Byte) {
        matches(input = input, expected = Unit)
    }

    fun <T> Parser<ByteInput, T>.matches(vararg input: Byte, expected: T) {
        val result = parse(input)
        result.assertIsSuccess(expected)

        val pushParser1 = pushParser()
        pushParser1.input(input)
        val result1 = pushParser1.endOfInput()
        result1.assertIsSuccess(expected)

        val pushParser2 = pushParser()
        for (index in input.indices) {
            pushParser2.input(byteArrayOf(input[index]))
        }
        val result2 = pushParser2.endOfInput()
        result2.assertIsSuccess(expected)
    }

    fun Parser<ByteInput, *>.doesNotMatch(vararg input: Byte, config: ByteParseFailureFixture.() -> Unit = {}) {
        val fixture = DefaultByteParseFailureFixture()
        fixture.config()

        val result = parse(input)
        result.assertIsFail(fixture.offset, fixture.message())
    }

    private fun <T> ParseResult<*, T>.assertIsSuccess(expected: T) {
        assertIs<ParseResult.Success<T>>(this)
        assertEquals(expected, value, "unexpected value")
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
        assertEquals(offset, position.offset, "unexpected offset")
        assertEquals(message, this.message)
    }

    interface CharParseFailureFixture {
        fun failAt(offset: Int, line: Int = 1, col: Int = offset + 1)

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
            return "Expected ${expect.joinToString(", ")}"
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
            return "Expected ${expect.joinToString(", ")}"
        }
    }

}