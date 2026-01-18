package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.*
import net.rubygrapefruit.parse.text.*
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.fail

abstract class AbstractParseTest {
    fun Parser<CharInput, Unit>.matches(input: String) {
        matches(input = input, expected = Unit)
    }

    @JvmName("expectingChars")
    fun <T> Parser<*, T>.expecting(config: CompiledParserFixture.() -> Unit) {
        val fixture = DefaultCompiledParserFixture()
        fixture.config()
        val compiledParser = compile<CharStream, T>()
        compiledParser.expecting(fixture)
    }

    fun <T> Parser<CharInput, T>.matches(input: String, expected: T, config: ParseFixture.() -> Unit = {}) {
        val fixture = DefaultParseFixture()
        fixture.config()

        fixture.tracing(this) { matches(fixture, input, expected) }
    }

    private fun <T> Parser<CharInput, T>.matches(fixture: DefaultParseFixture, input: String, expected: T) {
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

        fixture.tracing(this) { doesNotMatch(input, fixture) }
    }

    private fun Parser<CharInput, *>.doesNotMatch(input: String, fixture: DefaultCharParseFailureFixture) {
        fixture.debug("PARSE \"$input\"")
        val result = parse(input)
        result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.lineText(input), fixture.message())

        input.oneChunk {
            fixture.debug("PARSE ONE CHUNK")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.lineText(input), fixture.message())
        }

        input.chunkPerChar {
            fixture.debug("PARSE CHUNK PER CHAR")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.lineText(input), fixture.message())
        }

        input.maybeTwoChunks {
            fixture.debug("PARSE TWO CHUNKS")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.lineText(input), fixture.message())
        }
    }

    @JvmName("pushParseChars")
    private fun <T> Parser<CharInput, T>.pushParse(chunks: List<CharArray>): ParseResult<CharFailureContext, T> {
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

    @JvmName("expectingBytes")
    fun <T> Parser<ByteInput, T>.expecting(config: CompiledParserFixture.() -> Unit) {
        val fixture = DefaultCompiledParserFixture()
        fixture.config()

        val compiledParser = compile<ByteStream, T>()
        compiledParser.expecting(fixture)
    }

    fun Parser<ByteInput, Unit>.matches(vararg input: Byte, config: ParseFixture.() -> Unit = {}) {
        matches(input = input, expected = Unit, config = config)
    }

    fun Parser<ByteInput, List<Byte>>.matches(vararg input: Byte, expected: List<Byte>, config: ParseFixture.() -> Unit = {}) {
        // Compiler passes in list of Int
        matches(input = input, expected = expected.map { it.toByte() }, config = config, normalize = { it })
    }

    fun Parser<ByteInput, ByteArray>.matches(vararg input: Byte, expected: ByteArray, config: ParseFixture.() -> Unit = {}) {
        matches(input = input, expected = expected.toList(), config = config, normalize = { it.toList() })
    }

    fun <T> Parser<ByteInput, T>.matches(vararg input: Byte, expected: T, config: ParseFixture.() -> Unit = {}) {
        matches(input = input, expected = expected, config = config, normalize = { it })
    }

    private fun <T, E> Parser<ByteInput, T>.matches(
        vararg input: Byte,
        expected: E,
        config: ParseFixture.() -> Unit,
        normalize: (T) -> E
    ) {
        val fixture = DefaultParseFixture()
        fixture.config()

        fixture.tracing(this) { matches(input = input, expected = expected, fixture = fixture, normalize = normalize) }
    }

    private fun <T, E> Parser<ByteInput, T>.matches(
        vararg input: Byte,
        expected: E,
        fixture: DefaultParseFixture,
        normalize: (T) -> E
    ) {
        fixture.debug("PARSE ${input.joinToString(", ")}")

        val result = parse(input)
        result.assertIsSuccess(expected, normalize)

        input.oneChunk {
            fixture.debug("ONE CHUNK")
            val result = pushParse(it)
            result.assertIsSuccess(expected, normalize)
        }

        input.chunkPerByte {
            fixture.debug("ONE CHUNK PER BYTE")
            val result = pushParse(it)
            result.assertIsSuccess(expected, normalize)
        }

        input.maybeTwoChunks {
            fixture.debug("TWO CHUNKS")
            val result = pushParse(it)
            result.assertIsSuccess(expected, normalize)
        }
    }

    fun Parser<ByteInput, *>.doesNotMatch(vararg input: Byte, config: ByteParseFailureFixture.() -> Unit = {}) {
        val fixture = DefaultByteParseFailureFixture()
        fixture.config()

        fixture.tracing(this) { doesNotMatch(fixture = fixture, input = input) }
    }

    private fun Parser<ByteInput, *>.doesNotMatch(fixture: DefaultByteParseFailureFixture, vararg input: Byte) {
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
    private fun <T> Parser<ByteInput, T>.pushParse(chunks: List<ByteArray>): ParseResult<ByteFailureContext, T> {
        val pushParser = pushParser()
        for (chunk in chunks) {
            pushParser.input(chunk)
        }
        return pushParser.endOfInput()
    }

    private fun CompiledParser<*, *>.expecting(fixture: DefaultCompiledParserFixture) {
        assertEquals(fixture.emptyMatch, mayNotAdvanceOnMatch)
        assertEquals(fixture.message(), expectation.format())

        val pullParser = start()
        pullParser.expecting(fixture)
    }

    private fun PullParser<*, *>.expecting(fixture: DefaultCompiledParserFixture) {
        assertEquals(fixture.message(), expectation.format())
    }

    private fun <T> ParseResult<*, T>.assertIsSuccess(expected: T) {
        assertIsSuccess(expected) { it }
    }

    private fun <T, E> ParseResult<*, T>.assertIsSuccess(expected: E, normalize: (T) -> E) {
        when (this) {
            is ParseResult.Fail -> fail("Expected parse to succeed but failed at $context with $message")
            is ParseResult.Success -> {
                assertEquals(expected, normalize(value), "unexpected value")
                assertSame(value, get())
            }
        }
    }

    private fun ParseResult<CharFailureContext, *>.assertIsFail(offset: Int, line: Int, col: Int, failureLine: String, message: String) {
        when (this) {
            is ParseResult.Success -> fail("Expected parse failure at offset $offset with message: $message")
            is ParseResult.Fail -> {
                assertEquals(offset, context.position.offset, "unexpected offset")
                assertEquals(line, context.position.line, "unexpected line")
                assertEquals(col, context.position.col, "unexpected column")
                assertEquals(failureLine, context.lineText, "unexpected line text")
                assertEquals(message, this.message)

                try {
                    get()
                } catch (e: ParseException) {
                    val lines = e.message?.lines() ?: emptyList()
                    assertEquals(3, lines.size)
                    assertEquals("$line | $failureLine", lines[0])
                    assertEquals(message, lines[2])
                }
            }
        }
    }

    private fun ParseResult<ByteFailureContext, *>.assertIsFail(offset: Int, message: String) {
        when (this) {
            is ParseResult.Success -> fail("Expected parse failure at offset $offset with message: $message")
            is ParseResult.Fail -> {
                assertEquals(offset, context.position.offset, "unexpected offset")
                assertEquals(message, this.message)

                try {
                    get()
                } catch (e: ParseException) {
                    assertEquals("Offset: $offset: $message", e.message)
                }
            }
        }
    }

    interface CompiledParserFixture {
        fun emptyMatch()

        fun expect(text: String)

        fun expectLiteral(text: String)

        fun expectLiteral(byte: Byte)
    }

    private class HasExpectation {
        val expect = mutableListOf<String>()

        fun expect(text: String) {
            expect.add(text)
        }

        fun expectLiteral(text: String) {
            expect("\"$text\"")
        }

        fun expectLiteral(byte: Byte) {
            expect('x' + byte.toString(16).padStart(2, '0'))
        }

        fun message(): String {
            return "Expected ${expect.joinToString(", ")}"
        }
    }

    private class DefaultCompiledParserFixture : CompiledParserFixture {
        private val expected = HasExpectation()
        var emptyMatch = false

        override fun emptyMatch() {
            emptyMatch = true
        }

        override fun expect(text: String) {
            expected.expect(text)
        }

        override fun expectLiteral(text: String) {
            expected.expectLiteral(text)
        }

        override fun expectLiteral(byte: Byte) {
            expected.expectLiteral(byte)
        }

        fun message(): String {
            return expected.message()
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

        fun <IN, OUT> tracing(parser: Parser<IN, OUT>, action: Parser<IN, OUT>.() -> Unit) {
            if (log) {
                DiagnosticParser(parser, log).action()
            } else {
                parser.action()
                DiagnosticParser(parser, false).action()
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
        val expect = HasExpectation()

        override fun expect(text: String) {
            expect.expect(text)
        }

        fun message(): String {
            return expect.message()
        }
    }

    interface CharParseFailureFixture : ParseFailureFixture {
        fun failAt(offset: Int, line: Int = 1, col: Int = offset + 1)

        fun expectContext(textBeforeFailure: String, textAfterFailure: String)

        fun expectLiteral(text: String)
    }

    private class DefaultCharParseFailureFixture : DefaultParseFailureFixture(), CharParseFailureFixture {
        var offset = 0
        var line = 1
        var col = 1
        var textBefore: String? = null
        var textAfter: String? = null

        override fun expectLiteral(text: String) {
            expect.expectLiteral(text)
        }

        override fun failAt(offset: Int, line: Int, col: Int) {
            this.offset = offset
            this.line = line
            this.col = col
        }

        override fun expectContext(textBeforeFailure: String, textAfterFailure: String) {
            textBefore = textBeforeFailure
            textAfter = textAfterFailure
        }

        fun lineText(input: String): String {
            return if (textBefore != null && textAfter != null) {
                "$textBefore$textAfter"
            } else {
                input
            }
        }
    }

    interface ByteParseFailureFixture : ParseFailureFixture {
        fun failAt(offset: Int)

        fun expectLiteral(byte: Byte)
    }

    private class DefaultByteParseFailureFixture : DefaultParseFailureFixture(), ByteParseFailureFixture {
        var offset = 0

        override fun expectLiteral(byte: Byte) {
            expect.expectLiteral(byte)
        }

        override fun failAt(offset: Int) {
            this.offset = offset
        }
    }

}