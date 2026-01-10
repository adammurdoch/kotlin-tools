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
import kotlin.test.fail

abstract class AbstractParseTest {
    fun Parser<CharInput, Unit>.matches(input: String) {
        matches(input = input, expected = Unit)
    }

    fun <T> Parser<CharInput, T>.matches(input: String, expected: T, config: ParseFixture.() -> Unit = {}) {
        val fixture = DefaultParseFixture()
        fixture.config()

        fixture.debug("PARSE \"$input\"")
        val result = parse(input)
        result.assertIsSuccess(expected)

        input.oneChunk {
            fixture.debug("PARSE ONE CHUNK")
            val result = pushParse(it)
            result.assertIsSuccess(expected)
        }

        input.chunkPerChar {
            fixture.debug("PARSE CHUNK PER CHAR")
            val result = pushParse(it)
            result.assertIsSuccess(expected)
        }

        input.maybeTwoChunks {
            fixture.debug("PARSE TWO CHUNKS")
            val result = pushParse(it)
            result.assertIsSuccess(expected)
        }
    }

    fun Parser<CharInput, *>.doesNotMatch(input: String, config: CharParseFailureFixture.() -> Unit = {}) {
        val fixture = DefaultCharParseFailureFixture()
        fixture.config()

        fixture.debug("PARSE \"$input\"")
        val result = parse(input)
        result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.message())

        input.oneChunk {
            fixture.debug("PARSE ONE CHUNK")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.message())
        }

        input.chunkPerChar {
            fixture.debug("PARSE CHUNK PER CHAR")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.message())
        }

        input.maybeTwoChunks {
            fixture.debug("PARSE TWO CHUNKS")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.message())
        }
    }

    @JvmName("pushParseChars")
    private fun <T> Parser<CharInput, T>.pushParse(chunks: List<CharArray>): ParseResult<CharPosition, T> {
        val pushParser = pushParser()
        for (chunk in chunks) {
            pushParser.input(chunk)
        }
        return pushParser.endOfInput()
    }

    private fun String.oneChunk(action: (List<CharArray>) -> Unit) {
        action(listOf(toCharArray()))
    }

    private fun String.chunkPerChar(action: (List<CharArray>) -> Unit) {
        action(map { charArrayOf(it) })
    }

    private fun String.maybeTwoChunks(action: (List<CharArray>) -> Unit) {
        if (length < 2) {
            return
        }
        val split = length / 2
        action(listOf(toCharArray(0, split), toCharArray(split, length)))
    }

    fun Parser<ByteInput, Unit>.matches(vararg input: Byte) {
        matches(input = input, expected = Unit)
    }

    fun <T> Parser<ByteInput, T>.matches(vararg input: Byte, expected: T) {
        val result = parse(input)
        result.assertIsSuccess(expected)

        input.oneChunk {
            val result = pushParse(it)
            result.assertIsSuccess(expected)
        }

        input.chunkPerByte {
            val result = pushParse(it)
            result.assertIsSuccess(expected)
        }

        input.maybeTwoChunks {
            val result = pushParse(it)
            result.assertIsSuccess(expected)
        }
    }

    fun Parser<ByteInput, *>.doesNotMatch(vararg input: Byte, config: ByteParseFailureFixture.() -> Unit = {}) {
        val fixture = DefaultByteParseFailureFixture()
        fixture.config()

        fixture.debug("PARSE ${input.map { it.toString(16) }}")
        val result = parse(input)
        result.assertIsFail(fixture.offset, fixture.message())

        input.oneChunk {
            fixture.debug("PARSE ONE CHUNK")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.message())
        }

        input.chunkPerByte {
            fixture.debug("PARSE CHUNK PER BYTE")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.message())
        }

        input.maybeTwoChunks {
            fixture.debug("PARSE TWO CHUNKS")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.message())
        }
    }

    private fun ByteArray.oneChunk(action: (List<ByteArray>) -> Unit) {
        action(listOf(this))
    }

    private fun ByteArray.chunkPerByte(action: (List<ByteArray>) -> Unit) {
        action(map { byteArrayOf(it) })
    }

    private fun ByteArray.maybeTwoChunks(action: (List<ByteArray>) -> Unit) {
        if (size < 2) {
            return
        }
        val split = size / 2
        val b1 = ByteArray(split)
        copyInto(b1, 0, 0, split)
        val b2 = ByteArray(size - split)
        copyInto(b2, 0, split, size)
        action(listOf(b1, b2))
    }

    @JvmName("pushParseBytes")
    private fun <T> Parser<ByteInput, T>.pushParse(chunks: List<ByteArray>): ParseResult<BytePosition, T> {
        val pushParser = pushParser()
        for (chunk in chunks) {
            pushParser.input(chunk)
        }
        return pushParser.endOfInput()
    }

    private fun <T> ParseResult<*, T>.assertIsSuccess(expected: T) {
        when (this) {
            is ParseResult.Fail -> fail("Expected parse to succeed but failed at $position with $message")
            is ParseResult.Success -> {
                assertEquals(expected, value, "unexpected value")
            }
        }
    }

    private fun ParseResult<CharPosition, *>.assertIsFail(offset: Int, line: Int, col: Int, message: String) {
        when (this) {
            is ParseResult.Success -> fail("Expected parse failure at offset $offset with message: $message")
            is ParseResult.Fail -> {
                assertEquals(offset, position.offset, "unexpected offset")
                assertEquals(line, position.line, "unexpected line")
                assertEquals(col, position.col, "unexpected column")
                assertEquals(message, this.message)
            }
        }
    }

    private fun ParseResult<BytePosition, *>.assertIsFail(offset: Int, message: String) {
        when (this) {
            is ParseResult.Success -> fail("Expected parse failure at offset $offset with message: $message")
            is ParseResult.Fail -> {
                assertEquals(offset, position.offset, "unexpected offset")
                assertEquals(message, this.message)
            }
        }
    }

    interface ParseFixture {
        fun log()
    }

    private open class DefaultParseFixture : ParseFixture {
        var log = false

        override fun log() {
            log = true
        }

        fun debug(message: String) {
            if (log) {
                println("-> $message")
            }
        }
    }

    interface ParseFailureFixture : ParseFixture {
        fun expect(text: String)

        fun expectEndOfInput() {
            expect("end of input")
        }
    }

    private open class DefaultParseFailureFixture : DefaultParseFixture(), ParseFailureFixture {
        val expect = mutableListOf<String>()

        override fun expect(text: String) {
            expect.add(text)
        }

        fun message(): String {
            return "Expected ${expect.joinToString(", ")}"
        }
    }

    interface CharParseFailureFixture : ParseFailureFixture {
        fun failAt(offset: Int, line: Int = 1, col: Int = offset + 1)

        fun expectLiteral(text: String) {
            expect("\"$text\"")
        }
    }

    private class DefaultCharParseFailureFixture : DefaultParseFailureFixture(), CharParseFailureFixture {
        var offset = 0
        var line = 1
        var col = 1

        override fun failAt(offset: Int, line: Int, col: Int) {
            this.offset = offset
            this.line = line
            this.col = col
        }
    }

    interface ByteParseFailureFixture : ParseFailureFixture {
        fun failAt(offset: Int)
    }

    private class DefaultByteParseFailureFixture : DefaultParseFailureFixture(), ByteParseFailureFixture {
        var offset = 0

        override fun failAt(offset: Int) {
            this.offset = offset
        }
    }

}