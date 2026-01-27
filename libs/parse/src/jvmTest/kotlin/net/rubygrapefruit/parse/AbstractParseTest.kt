package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.binary.*
import net.rubygrapefruit.parse.combinators.*
import net.rubygrapefruit.parse.general.EndOfInputParser
import net.rubygrapefruit.parse.general.MatchedInputParser
import net.rubygrapefruit.parse.general.SingleInputCompiledParser
import net.rubygrapefruit.parse.general.SucceedParser
import net.rubygrapefruit.parse.text.*
import kotlin.test.*

abstract class AbstractParseTest {
    fun Parser<CharInput, Unit>.matches(input: String) {
        matches(input = input, expected = Unit)
    }

    fun bytes(vararg items: Byte): List<Byte> {
        return items.toList()
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
        fixture.debug("PARSE [${input.joinToString { format(it) }}]")
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
        fixture.debug("PARSE [${input.joinToString { format(it) }}]")
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
        fixture.inspect(this)

        val pullParser = start()
        pullParser.expecting(fixture)
    }

    private fun PullParser<*, *>.expecting(fixture: DefaultCompiledParserFixture) {
        val failure = stop()
        assertEquals(0, failure.index)
        assertEquals(fixture.message(), failure.expected.format())
    }

    private fun <T> ParseResult<*, T>.assertIsSuccess(expected: T) {
        assertIsSuccess(expected) { it }
    }

    private fun <T, E> ParseResult<*, T>.assertIsSuccess(expected: E, normalize: (T) -> E) {
        when (this) {
            is ParseResult.Fail -> fail("Expected parse to succeed but failed with $context and $message")
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
                assertEquals(offset, context.position.offset, "unexpected offset for failure '$message'")
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
        fun expectSucceed(result: Any = Unit)

        fun expectEndOfInput(result: Any = Unit)

        fun expectLiteral(text: String, result: Any = Unit)

        fun expectLiteral(vararg bytes: Byte, result: Any = Unit)

        fun expectOneOf(vararg chars: Char, hasResult: Boolean = true)

        fun expectOneOf(vararg bytes: Byte, hasResult: Boolean = true)

        fun expectMatch(config: CompiledParserFixture.() -> Unit)

        fun expectNot(config: CompiledParserFixture.() -> Unit)

        fun expectChoice(config: CompiledParserFixture.() -> Unit)

        fun expectSequence(config: CompiledParserFixture.() -> Unit)

        fun expectOneOrMore(hasResult: Boolean = true, config: CompiledParserFixture.() -> Unit)

        fun expectZero()

        fun expectZeroOrMoreSingleInput(vararg literals: String, hasResult: Boolean = true)

        fun expectZeroOrMoreSingleInput(vararg literals: Byte, hasResult: Boolean = true)

        fun expectMap(config: CompiledParserFixture.() -> Unit)

        fun expectDecide(config: CompiledParserFixture.() -> Unit)

        fun expectRecursive(config: CompiledParserFixture.() -> Unit)

        fun expectRecurses()
    }

    private class HasExpectation {
        val expect = mutableListOf<String>()

        fun expect(text: String) {
            expect.add(text)
        }

        fun expectLiteral(text: String) {
            if (text == "\n") {
                expect("new line")
            } else {
                expect("\"$text\"")
            }
        }

        fun expectLiteral(byte: Byte) {
            expect('x' + byte.toString(16).padStart(2, '0'))
        }

        fun message(): String {
            return "Expected ${expect.sorted().joinToString(", ")}"
        }
    }

    private sealed interface Inspector {
        val expected: List<String>

        val mayBeEmpty: Boolean
            get() = false

        fun inspect(parser: CompiledParser<*, *>)

        data class IsSucceed(val result: Any) : Inspector {
            override val expected: List<String>
                get() = emptyList()

            override val mayBeEmpty: Boolean
                get() = true

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<SucceedParser.SucceedCompiledParser<*, *>>(parser)
                assertEquals(result, parser.result)
            }
        }

        data class IsEndOfInput(val result: Any) : Inspector {
            override val expected: List<String>
                get() = listOf("end of input")

            override val mayBeEmpty: Boolean
                get() = true

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<EndOfInputParser.EndOfInputCompiledParser<*, *>>(parser)
                assertEquals(result, parser.result)
            }
        }

        class IsMatchInput(val inspector: Inspector) : Inspector {
            override val expected: List<String>
                get() = inspector.expected

            override val mayBeEmpty: Boolean
                get() = inspector.mayBeEmpty

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<MatchedInputParser.MatchedInputCompiledParser<*>>(parser)
                inspector.inspect(parser.parser)
            }
        }

        class IsNot(val inspector: Inspector) : Inspector {
            override val expected: List<String>
                get() = inspector.expected.map { "not $it" }

            override val mayBeEmpty: Boolean
                get() = true

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<NotParser.NotCompiledParser<*>>(parser)
                inspector.inspect(parser.parser)
            }
        }

        class IsChoice(val choices: List<Inspector>) : Inspector {
            override val expected: List<String>
                get() = choices.flatMap { it.expected }

            override val mayBeEmpty: Boolean
                get() = choices.any { it.mayBeEmpty }

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<ChoiceParser.ChoiceCompiledParser<*, *>>(parser)
                assertEquals(choices.size, parser.parsers.size)
                for (index in choices.indices) {
                    choices[index].inspect(parser.parsers[index])
                }
            }
        }

        class IsSequence(val a: Inspector, val b: Inspector) : Inspector {
            override val expected: List<String>
                get() = if (a.mayBeEmpty) a.expected + b.expected else a.expected

            override val mayBeEmpty: Boolean
                get() = a.mayBeEmpty && b.mayBeEmpty

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<Sequence2Parser.Sequence2CompiledParser<*, *, *, *>>(parser)
                a.inspect(parser.a)
                b.inspect(parser.b)
            }
        }

        class IsMap(val inspector: Inspector) : Inspector {
            override val expected: List<String>
                get() = inspector.expected

            override val mayBeEmpty: Boolean
                get() = inspector.mayBeEmpty

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<MapParser.MapCompiledParser<*, *, *>>(parser)
                inspector.inspect(parser.parser)
            }
        }

        class IsDecide(val inspector: Inspector, val second: Inspector) : Inspector {
            override val expected: List<String>
                get() = if (inspector.mayBeEmpty) inspector.expected + second.expected else inspector.expected

            override val mayBeEmpty: Boolean
                get() = false

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<DecideParser.DecideCompiledParser<*, *, *>>(parser)
                inspector.inspect(parser.parser)
            }
        }

        class IsOneOrMore(val inspector: Inspector, val hasResult: Boolean) : Inspector {
            override val expected: List<String>
                get() = inspector.expected

            override val mayBeEmpty: Boolean
                get() = inspector.mayBeEmpty

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<ZeroOrMoreParser.OptionCompiledParser<*, *, *>>(parser)
                inspector.inspect(parser.option)
                if (hasResult) {
                    assertIs<ListAccumulator<*>>(parser.previous)
                } else {
                    assertIs<UnitAccumulator>(parser.previous)
                }
            }
        }

        data object IsZero : Inspector {
            override val expected: List<String>
                get() = emptyList()

            override val mayBeEmpty: Boolean
                get() = true

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<ZeroOrMoreParser.EmptyCompiledParser<*, *, *>>(parser)
            }
        }

        data class IsZeroOrMoreSingleInput(val expectation: HasExpectation, val hasResult: Boolean) : Inspector {
            override val expected: List<String>
                get() = expectation.expect

            override val mayBeEmpty: Boolean
                get() = true

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<ZeroOrMoreSingleInputCompiledParser<*, *>>(parser)
                if (hasResult) {
                    assertIs<ListRangeAccumulator<*, *>>(parser.accumulator)
                } else {
                    assertIs<UnitRangeAccumulator>(parser.accumulator)
                }
            }
        }

        data class IsCharLiteral(val text: String, val result: Any) : Inspector {
            override val expected: List<String>
                get() {
                    val expected = HasExpectation()
                    expected.expectLiteral(text)
                    return expected.expect
                }

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<ParserBuilderAdaptor<*, *>>(parser)
                assertIs<CharLiteralParser<*>>(parser.parser)
                assertEquals(result, parser.parser.result)
            }
        }

        class IsByteLiteral(val bytes: ByteArray, val result: Any) : Inspector {
            override val expected: List<String>
                get() {
                    val expected = HasExpectation()
                    expected.expectLiteral(bytes.first())
                    return expected.expect
                }

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<ParserBuilderAdaptor<*, *>>(parser)
                assertIs<ByteLiteralParser<*>>(parser.parser)
                assertEquals(result, parser.parser.result)
            }
        }

        data class IsOneOfChar(val chars: List<Char>, val hasResult: Boolean) : Inspector {
            override val expected: List<String>
                get() {
                    val expected = HasExpectation()
                    for (ch in chars) {
                        expected.expectLiteral(ch.toString())
                    }
                    return expected.expect
                }

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<SingleInputCompiledParser<*, *>>(parser)
                assertIs<OneOfCharParser>(parser.parser)
                assertEquals(chars, parser.parser.chars)
                if (hasResult) {
                    assertIs<NextValueExtractor<*, *>>(parser.extractor)
                } else {
                    assertIs<UnitExtractor>(parser.extractor)
                }
            }
        }

        data class IsOneOfByte(val bytes: List<Byte>, val hasResult: Boolean) : Inspector {
            override val expected: List<String>
                get() {
                    val expected = HasExpectation()
                    for (b in bytes) {
                        expected.expectLiteral(b)
                    }
                    return expected.expect
                }

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<SingleInputCompiledParser<*, *>>(parser)
                assertIs<OneOfByteParser>(parser.parser)
                assertEquals(bytes, parser.parser.bytes)
                if (hasResult) {
                    assertIs<NextValueExtractor<*, *>>(parser.extractor)
                } else {
                    assertIs<UnitExtractor>(parser.extractor)
                }
            }
        }

        class IsRecursive(val inspector: Inspector) : Inspector {
            override val expected: List<String>
                get() = inspector.expected

            override val mayBeEmpty: Boolean
                get() = inspector.mayBeEmpty

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<DefaultRecursiveParser.RecursiveCompiledParser<*, *>>(parser)
                inspector.inspect(parser.parser!!)
            }
        }

        data object Recurses : Inspector {
            override val expected: List<String>
                get() = emptyList()

            override val mayBeEmpty: Boolean
                get() = true

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<DefaultRecursiveParser.RecursiveCompiledParser<*, *>>(parser)
            }
        }
    }

    private class DefaultCompiledParserFixture : CompiledParserFixture {
        private val inspectors = mutableListOf<Inspector>()

        fun choices(): List<Inspector> {
            assertTrue(inspectors.isNotEmpty(), "no expected parsers defined")
            return inspectors
        }

        fun inspector(): Inspector {
            assertEquals(1, inspectors.size, "expected one parser defined")
            return inspectors.first()
        }

        fun inspect(parser: CompiledParser<*, *>) {
            inspector().inspect(parser)
        }

        override fun expectSucceed(result: Any) {
            inspectors.add(Inspector.IsSucceed(result))
        }

        override fun expectEndOfInput(result: Any) {
            inspectors.add(Inspector.IsEndOfInput(result))
        }

        override fun expectLiteral(text: String, result: Any) {
            inspectors.add(Inspector.IsCharLiteral(text, result))
        }

        override fun expectLiteral(vararg bytes: Byte, result: Any) {
            inspectors.add(Inspector.IsByteLiteral(bytes, result))
        }

        override fun expectOneOf(vararg chars: Char, hasResult: Boolean) {
            inspectors.add(Inspector.IsOneOfChar(chars.toList(), hasResult))
        }

        override fun expectOneOf(vararg bytes: Byte, hasResult: Boolean) {
            inspectors.add(Inspector.IsOneOfByte(bytes.toList(), hasResult))
        }

        override fun expectMatch(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsMatchInput(fixture.inspector()))
        }

        override fun expectNot(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsNot(fixture.choices().first()))
        }

        override fun expectChoice(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsChoice(fixture.choices()))
        }

        override fun expectSequence(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsSequence(fixture.choices()[0], fixture.choices()[1]))
        }

        override fun expectOneOrMore(hasResult: Boolean, config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsOneOrMore(fixture.inspector(), hasResult))
        }

        override fun expectZero() {
            inspectors.add(Inspector.IsZero)
        }

        override fun expectZeroOrMoreSingleInput(vararg literals: String, hasResult: Boolean) {
            val expected = HasExpectation()
            for (text in literals) {
                expected.expectLiteral(text)
            }
            inspectors.add(Inspector.IsZeroOrMoreSingleInput(expected, hasResult))
        }

        override fun expectZeroOrMoreSingleInput(vararg literals: Byte, hasResult: Boolean) {
            val expected = HasExpectation()
            for (b in literals) {
                expected.expectLiteral(b)
            }
            inspectors.add(Inspector.IsZeroOrMoreSingleInput(expected, hasResult))
        }

        override fun expectMap(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsMap(fixture.inspector()))
        }

        override fun expectDecide(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsDecide(fixture.choices()[0], fixture.choices()[1]))
        }

        override fun expectRecursive(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsRecursive(fixture.inspector()))
        }

        override fun expectRecurses() {
            inspectors.add(Inspector.Recurses)
        }

        fun message(): String {
            val expected = HasExpectation()
            expected.expect.addAll(inspectors.first().expected)
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