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
    fun Parser<TextInput, Unit>.matches(input: String, config: ParseFixture.() -> Unit = {}) {
        matches(input = input, expected = Unit, config = config)
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

    fun <T> Parser<TextInput, T>.matches(input: String, expected: T, config: ParseFixture.() -> Unit = {}) {
        val fixture = DefaultParseFixture()
        fixture.config()

        fixture.tracing(this, fixture.steps) {
            matchesString(fixture, input, expected)
        }
        fixture.tracing(this, null) {
            matchesChunks(fixture, input, expected)
        }
    }

    private fun <T> Parser<TextInput, T>.matchesString(fixture: DefaultParseFixture, input: String, expected: T) {
        fixture.debug("PARSE \"$input\"")
        val result = parse(input)
        result.assertIsSuccess(expected)
    }

    private fun <T> Parser<TextInput, T>.matchesChunks(fixture: DefaultParseFixture, input: String, expected: T) {
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

    fun Parser<TextInput, *>.doesNotMatch(input: String, config: TextParseFailureFixture.() -> Unit = {}) {
        val fixture = DefaultTextParseFailureFixture()
        fixture.config()

        fixture.tracing(this, fixture.steps) {
            doesNotMatchString(input, fixture)
        }
        fixture.tracing(this, null) {
            doesNotMatchChunks(input, fixture)
        }
    }

    private fun Parser<TextInput, *>.doesNotMatchString(input: String, fixture: DefaultTextParseFailureFixture) {
        fixture.debug("PARSE \"$input\"")
        val result = parse(input)
        result.assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.lineText(input), fixture.message())
    }

    private fun Parser<TextInput, *>.doesNotMatchChunks(input: String, fixture: DefaultTextParseFailureFixture) {
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
    private fun <T> Parser<TextInput, T>.pushParse(chunks: List<CharArray>): ParseResult<TextFailureContext, T> {
        val pushParser = pushParser()
        for (chunk in chunks) {
            val failure = pushParser.input(chunk)
            if (failure != null) {
                return failure
            }
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
    fun <T> Parser<BinaryInput, T>.expecting(config: CompiledParserFixture.() -> Unit) {
        val fixture = DefaultCompiledParserFixture()
        fixture.config()

        val compiledParser = compile<ByteStream, T>()
        compiledParser.expecting(fixture)
    }

    fun Parser<BinaryInput, Unit>.matches(vararg input: Byte, config: ParseFixture.() -> Unit = {}) {
        matches(input = input, expected = Unit, config = config)
    }

    fun Parser<BinaryInput, ByteArray>.matches(vararg input: Byte, expected: ByteArray, config: ParseFixture.() -> Unit = {}) {
        matches(input = input, expected = expected.toList(), config = config, normalize = { it.toList() })
    }

    fun <T> Parser<BinaryInput, T>.matches(vararg input: Byte, expected: T, config: ParseFixture.() -> Unit = {}) {
        matches(input = input, expected = expected, config = config, normalize = { it })
    }

    private fun <T, E> Parser<BinaryInput, T>.matches(
        vararg input: Byte,
        expected: E,
        config: ParseFixture.() -> Unit,
        normalize: (T) -> E
    ) {
        val fixture = DefaultParseFixture()
        fixture.config()

        fixture.tracing(this, fixture.steps) {
            matchesArray(input = input, expected = expected, fixture = fixture, normalize = normalize)
        }
        fixture.tracing(this, null) {
            matchesChunks(input = input, expected = expected, fixture = fixture, normalize = normalize)
        }
    }

    private fun <T, E> Parser<BinaryInput, T>.matchesArray(
        vararg input: Byte,
        expected: E,
        fixture: DefaultParseFixture,
        normalize: (T) -> E
    ) {
        fixture.debug("PARSE [${input.joinToString { format(it) }}]")
        val result = parse(input)
        result.assertIsSuccess(expected, normalize)
    }

    private fun <T, E> Parser<BinaryInput, T>.matchesChunks(
        vararg input: Byte,
        expected: E,
        fixture: DefaultParseFixture,
        normalize: (T) -> E
    ) {
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

    fun Parser<BinaryInput, *>.doesNotMatch(vararg input: Byte, config: BinaryParseFailureFixture.() -> Unit = {}) {
        val fixture = DefaultBinaryParseFailureFixture()
        fixture.config()

        fixture.tracing(this, fixture.steps) {
            doesNotMatchArray(fixture = fixture, input = input)
        }
        fixture.tracing(this, null) {
            doesNotMatchChunks(fixture = fixture, input = input)
        }
    }

    private fun Parser<BinaryInput, *>.doesNotMatchArray(fixture: DefaultBinaryParseFailureFixture, vararg input: Byte) {
        fixture.debug("PARSE [${input.joinToString { format(it) }}]")
        val result = parse(input)
        result.assertIsFail(fixture.offset, fixture.failureContext(input), fixture.message())
    }

    private fun Parser<BinaryInput, *>.doesNotMatchChunks(fixture: DefaultBinaryParseFailureFixture, vararg input: Byte) {
        fixture.debug("PARSE [${input.joinToString { format(it) }}]")
        val result = parse(input)
        result.assertIsFail(fixture.offset, fixture.failureContext(input), fixture.message())

        input.oneChunk {
            fixture.debug("PARSE ONE CHUNK")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.failureContext(input), fixture.message())
        }

        input.chunkPerByte {
            fixture.debug("PARSE CHUNK PER BYTE")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.failureContext(input), fixture.message())
        }

        input.maybeTwoChunks {
            fixture.debug("PARSE TWO CHUNKS")
            val result = pushParse(it)
            result.assertIsFail(fixture.offset, fixture.failureContext(input), fixture.message())
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
    private fun <T> Parser<BinaryInput, T>.pushParse(chunks: List<ByteArray>): ParseResult<BinaryFailureContext, T> {
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
        assertEquals(fixture.message(), failure.expected.expectation().format())
    }

    protected fun ParseResult<*, Unit>.assertIsSuccess() {
        assertIsSuccess(Unit) { it }
    }

    protected fun <T> ParseResult<*, T>.assertIsSuccess(expected: T) {
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

    @JvmName("assertIsTextFail")
    protected fun ParseResult<TextFailureContext, *>?.assertIsFail(config: TextParseFailureFixture.() -> Unit) {
        assertNotNull(this)

        val fixture = DefaultTextParseFailureFixture()
        fixture.config()

        assertIsFail(fixture.offset, fixture.line, fixture.col, fixture.lineText(""), fixture.message())
    }

    private fun ParseResult<TextFailureContext, *>.assertIsFail(offset: Int, line: Int, col: Int, failureLine: String, message: String) {
        when (this) {
            is ParseResult.Success -> fail("Expected parse failure at offset $offset with message: $message")
            is ParseResult.Fail -> {
                assertEquals(message, this.message)
                assertEquals(offset, context.position.offset, "unexpected offset for failure $message")
                assertEquals(line, context.position.line, "unexpected line")
                assertEquals(col, context.position.col, "unexpected column")
                assertEquals(failureLine, context.lineText, "unexpected line text")

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

    @JvmName("assertIsBinaryFail")
    protected fun ParseResult<BinaryFailureContext, *>?.assertIsFail(config: BinaryParseFailureFixture.() -> Unit) {
        assertNotNull(this)

        val fixture = DefaultBinaryParseFailureFixture()
        fixture.config()

        assertIsFail(fixture.offset, fixture.failureContext(byteArrayOf()), fixture.message())
    }

    private fun ParseResult<BinaryFailureContext, *>.assertIsFail(offset: Int, failureContext: String, message: String) {
        when (this) {
            is ParseResult.Success -> fail("Expected parse failure at offset $offset with message: $message")
            is ParseResult.Fail -> {
                assertEquals(message, this.message)
                assertEquals(offset, context.position.offset, "unexpected offset for failure $message")
                assertEquals(failureContext, context.found)

                try {
                    get()
                } catch (e: ParseException) {
                    assertEquals("Offset: $offset: $message. Found: $failureContext", e.message)
                }
            }
        }
    }

    interface CompiledParserFixture {
        fun expectSucceed(result: Any? = Unit)

        fun expectEndOfInput(result: Any = Unit)

        fun expectLiteral(text: String, result: Any = Unit)

        fun expectLiteral(vararg bytes: Byte, result: Any = Unit)

        fun expectOneChar(hasResult: Boolean = true)

        fun expectOneByte(hasResult: Boolean = true)

        fun expectOneOf(vararg chars: Char, hasResult: Boolean = true) {
            expectOneOf(chars.toList(), hasResult = hasResult)
        }

        fun expectOneInRange(from: Char, to: Char, hasResult: Boolean = true)

        fun expectOneOf(chars: List<Char>, hasResult: Boolean = true)

        fun expectOneOf(vararg chars: String, hasResult: Boolean = true) {
            expectOneOf(chars.map { it.first() }, hasResult = hasResult)
        }

        fun expectOneOf(vararg bytes: Byte, hasResult: Boolean = true)

        fun expectOneInRange(from: Byte, to: Byte, hasResult: Boolean = true)

        fun expectMatch(config: CompiledParserFixture.() -> Unit)

        fun expectNot(config: CompiledParserFixture.() -> Unit)

        fun expectChoice(config: CompiledParserFixture.() -> Unit)

        fun expectSequence(config: CompiledParserFixture.() -> Unit)

        fun expectZeroOrMore(hasResult: Boolean = true, config: CompiledParserFixture.() -> Unit)

        fun expectOneOrMore(hasResult: Boolean = true, config: CompiledParserFixture.() -> Unit)

        fun expectZeroOrMoreSingleInput(hasResult: Boolean = true, config: CompiledParserFixture.() -> Unit)

        fun expectMap(config: CompiledParserFixture.() -> Unit)

        fun expectDescribed(description: String, config: CompiledParserFixture.() -> Unit)

        fun expectConsume(config: CompiledParserFixture.() -> Unit)

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
            expect(format(text))
        }

        fun expectLiteral(byte: Byte) {
            expect(format(byte))
        }

        fun expectRange(from: Char, to: Char) {
            expect("${format(from)}..${format(to)}")
        }

        fun expectRange(from: Byte, to: Byte) {
            expect("${format(from)}..${format(to)}")
        }

        fun message(): String {
            return "Expected ${expect.sorted().joinToString(", ")}"
        }

        private fun format(text: String): String {
            return if (text == "\n") {
                "new line"
            } else {
                "\"$text\""
            }
        }

        private fun format(byte: Byte): String {
            return if (byte == 0.toByte()) {
                "x0"
            } else {
                'x' + byte.toString(16).padStart(2, '0')
            }
        }
    }

    private sealed interface Inspector {
        val expected: List<String>

        val mayBeEmpty: Boolean
            get() = false

        fun inspect(parser: CompiledParser<*, *>)

        data class IsSucceed(val result: Any?) : Inspector {
            override val expected: List<String>
                get() = emptyList()

            override val mayBeEmpty: Boolean
                get() = true

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<SucceedParser.SucceedCompiledParser<*, *>>(parser)
                assertEquals(result, parser.result.get())
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
                get() = choices.flatMap { it.expected }.distinct()

            override val mayBeEmpty: Boolean
                get() = choices.any { it.mayBeEmpty }

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<ChoiceParser.ChoiceCompiledParser<*, *>>(parser)
                assertEquals(choices.size, parser.options.size)
                for (index in choices.indices) {
                    choices[index].inspect(parser.options[index])
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

        class IsDescribed(val description: String, val inspector: Inspector) : Inspector {
            override val expected: List<String>
                get() = listOf(description)

            override val mayBeEmpty: Boolean
                get() = inspector.mayBeEmpty

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<DescribingParser.DescribingCompiledParser<*, *>>(parser)
                inspector.inspect(parser.parser)
            }
        }

        class IsConsume(val inspector: Inspector) : Inspector {
            override val expected: List<String>
                get() = inspector.expected

            override val mayBeEmpty: Boolean
                get() = inspector.mayBeEmpty

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<ConsumeParser.ConsumeCompiledParser<*, *>>(parser)
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

        class IsZeroOrMore(val inspector: Inspector, val separator: Inspector?, val hasResult: Boolean) : Inspector {
            override val expected: List<String>
                get() = inspector.expected

            override val mayBeEmpty: Boolean
                get() = true

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<ZeroOrMoreParser.ZeroOrMoreCompiledParser<*, *, *>>(parser)
                inspector.inspect(parser.option)
                if (separator != null) {
                    IsSequence(separator, inspector).inspect(parser.tail)
                } else {
                    inspector.inspect(parser.tail)
                }
                if (hasResult) {
                    assertIs<ListAccumulator<*>>(parser.initial)
                } else {
                    assertIs<UnitAccumulator>(parser.initial)
                }
            }
        }

        data class IsZeroOrMoreSingleInput(val inspector: IsSingleInput, val hasResult: Boolean) : Inspector {
            override val expected: List<String>
                get() = inspector.expected

            override val mayBeEmpty: Boolean
                get() = true

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<ZeroOrMoreSingleInputCompiledParser<*, *>>(parser)
                inspector.inspect(parser.parser)
                if (hasResult) {
                    assertIs<ListRangeAccumulator<*, *>>(parser.accumulator)
                } else {
                    assertIs<UnitRangeAccumulator>(parser.accumulator)
                }
            }
        }

        class IsOneOrMore(val inspector: Inspector, val separator: Inspector?, val hasResult: Boolean) : Inspector {
            override val expected: List<String>
                get() = inspector.expected

            override val mayBeEmpty: Boolean
                get() = inspector.mayBeEmpty

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<OneOrMoreParser.OneOrMoreCompiledParser<*, *, *>>(parser)
                inspector.inspect(parser.parser)
                if (separator != null) {
                    IsSequence(separator, inspector).inspect(parser.tail)
                } else {
                    inspector.inspect(parser.tail)
                }
                if (hasResult) {
                    assertIs<ListAccumulator<*>>(parser.initial)
                } else {
                    assertIs<UnitAccumulator>(parser.initial)
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
                assertIs<TextLiteralParser<*>>(parser.parser)
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
                assertIs<BinaryLiteralParser<*>>(parser.parser)
                assertEquals(result, parser.parser.result)
            }
        }

        sealed class IsSingleInput(val hasResult: Boolean) : Inspector {
            abstract fun inspect(parser: SingleInputParser<*>)

            override fun inspect(parser: CompiledParser<*, *>) {
                assertIs<SingleInputCompiledParser<*, *>>(parser)
                inspect(parser.parser)
                if (hasResult) {
                    assertIs<NextValueExtractor<*, *>>(parser.extractor)
                } else {
                    assertIs<UnitExtractor>(parser.extractor)
                }
            }
        }

        class IsOneChar(hasResult: Boolean) : IsSingleInput(hasResult) {
            override val expected: List<String>
                get() = listOf("any character")

            override fun inspect(parser: SingleInputParser<*>) {
                assertIs<OneCharParser>(parser)
            }
        }

        class IsOneByte(hasResult: Boolean) : IsSingleInput(hasResult) {
            override val expected: List<String>
                get() = listOf("any byte")

            override fun inspect(parser: SingleInputParser<*>) {
                assertIs<OneByteParser>(parser)
            }
        }

        class IsOneOfChar(val chars: List<Char>, hasResult: Boolean) : IsSingleInput(hasResult) {
            override val expected: List<String>
                get() {
                    val expected = HasExpectation()
                    for (ch in chars) {
                        expected.expectLiteral(ch.toString())
                    }
                    return expected.expect
                }

            override fun inspect(parser: SingleInputParser<*>) {
                assertIs<OneOfCharParser>(parser)
            }
        }

        class IsOneInCharRange(val from: Char, val to: Char, hasResult: Boolean) : IsSingleInput(hasResult) {
            override val expected: List<String>
                get() {
                    val expected = HasExpectation()
                    expected.expectRange(from, to)
                    return expected.expect
                }

            override fun inspect(parser: SingleInputParser<*>) {
                assertIs<OneInCharRangeParser>(parser)
            }
        }

        class IsOneOfByte(val bytes: List<Byte>, hasResult: Boolean) : IsSingleInput(hasResult) {
            override val expected: List<String>
                get() {
                    val expected = HasExpectation()
                    for (b in bytes) {
                        expected.expectLiteral(b)
                    }
                    return expected.expect
                }

            override fun inspect(parser: SingleInputParser<*>) {
                assertIs<OneOfByteParser>(parser)
            }
        }

        class IsOneInByteRange(val from: Byte, val to: Byte, hasResult: Boolean) : IsSingleInput(hasResult) {
            override val expected: List<String>
                get() {
                    val expected = HasExpectation()
                    expected.expectRange(from, to)
                    return expected.expect
                }

            override fun inspect(parser: SingleInputParser<*>) {
                assertIs<OneInByteRangeParser>(parser)
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

        fun choices(size: Int): List<Inspector> {
            assertEquals(size, inspectors.size)
            return inspectors
        }

        fun inspector(): Inspector {
            assertEquals(1, inspectors.size, "expected one parser defined")
            return inspectors.first()
        }

        fun inspect(parser: CompiledParser<*, *>) {
            inspector().inspect(parser)
        }

        override fun expectSucceed(result: Any?) {
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

        override fun expectOneChar(hasResult: Boolean) {
            inspectors.add(Inspector.IsOneChar(hasResult))
        }

        override fun expectOneByte(hasResult: Boolean) {
            inspectors.add(Inspector.IsOneByte(hasResult))
        }

        override fun expectOneOf(chars: List<Char>, hasResult: Boolean) {
            inspectors.add(Inspector.IsOneOfChar(chars, hasResult))
        }

        override fun expectOneInRange(from: Char, to: Char, hasResult: Boolean) {
            inspectors.add(Inspector.IsOneInCharRange(from, to, hasResult))
        }

        override fun expectOneOf(vararg bytes: Byte, hasResult: Boolean) {
            inspectors.add(Inspector.IsOneOfByte(bytes.toList(), hasResult))
        }

        override fun expectOneInRange(from: Byte, to: Byte, hasResult: Boolean) {
            inspectors.add(Inspector.IsOneInByteRange(from, to, hasResult))
        }

        override fun expectMatch(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsMatchInput(fixture.inspector()))
        }

        override fun expectNot(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsNot(fixture.inspector()))
        }

        override fun expectChoice(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsChoice(fixture.choices()))
        }

        override fun expectSequence(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            val choices = fixture.choices(2)
            inspectors.add(Inspector.IsSequence(choices[0], choices[1]))
        }

        override fun expectZeroOrMore(hasResult: Boolean, config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            val choices = fixture.choices()
            inspectors.add(Inspector.IsZeroOrMore(choices.first(), choices.getOrNull(1), hasResult))
        }

        override fun expectZeroOrMoreSingleInput(hasResult: Boolean, config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsZeroOrMoreSingleInput(fixture.inspector() as Inspector.IsSingleInput, hasResult))
        }

        override fun expectOneOrMore(hasResult: Boolean, config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            val choices = fixture.choices()
            inspectors.add(Inspector.IsOneOrMore(choices.first(), choices.getOrNull(1), hasResult))
        }

        override fun expectConsume(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsConsume(fixture.inspector()))
        }

        override fun expectDescribed(description: String, config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsDescribed(description, fixture.inspector()))
        }

        override fun expectMap(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            inspectors.add(Inspector.IsMap(fixture.inspector()))
        }

        override fun expectDecide(config: CompiledParserFixture.() -> Unit) {
            val fixture = DefaultCompiledParserFixture()
            fixture.config()
            val choices = fixture.choices(2)
            inspectors.add(Inspector.IsDecide(choices[0], choices[1]))
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

        /**
         * Declares the steps that parsing will take when parsing the whole input
         */
        fun steps(config: ParseStepsFixture.() -> Unit)
    }

    private open class DefaultParseFixture : ParseFixture {
        var log = false
        var steps: DefaultParseStepsFixture? = null
            private set

        protected open val fails: Boolean
            get() = false

        override fun log() {
            log = true
        }

        override fun steps(config: ParseStepsFixture.() -> Unit) {
            val steps = DefaultParseStepsFixture(fails)
            steps.config()
            this.steps = steps
        }

        fun debug(message: String) {
            if (log) {
                println("-> $message")
            }
        }

        fun <IN, OUT> tracing(parser: Parser<IN, OUT>, expectedSteps: DefaultParseStepsFixture?, action: Parser<IN, OUT>.() -> Unit) {
            val steps = mutableListOf<Step>()
            val tracingListener = object : DiagnosticParser.Listener {
                override fun requireMore(advance: Int, commit: Int) {
                    steps.add(Step(advance, commit))
                }
            }
            if (log) {
                DiagnosticParser.of(parser, true, tracingListener).action()
            } else {
                parser.action()
                DiagnosticParser.of(parser, false, tracingListener).action()
            }
            if (expectedSteps != null) {
                assertEquals(expectedSteps.steps(), steps)
            }
        }
    }

    interface ParseStepsFixture {
        /**
         * Advances and commits the given number of input values.
         */
        fun commit(count: Int)

        /**
         * Advances the given number of input values.
         */
        fun advance(count: Int, commit: Int = 0)
    }

    private class DefaultParseStepsFixture(val fails: Boolean) : ParseStepsFixture {
        private val steps = mutableListOf<Step>()

        fun steps(): List<Step> {
            return if (fails) {
                steps
            } else {
                steps + listOf(Step(0, 0))
            }
        }

        override fun commit(count: Int) {
            steps.add(Step(count, count))
        }

        override fun advance(count: Int, commit: Int) {
            steps.add(Step(count, commit))
        }
    }

    private data class Step(val advance: Int, val commit: Int)

    interface ParseFailureFixture : ParseFixture {
        fun expect(text: String)

        fun expectEndOfInput() {
            expect("end of input")
        }
    }

    private open class DefaultParseFailureFixture : DefaultParseFixture(), ParseFailureFixture {
        val expect = HasExpectation()

        override val fails: Boolean
            get() = true

        override fun expect(text: String) {
            expect.expect(text)
        }

        fun message(): String {
            return expect.message()
        }
    }

    interface TextParseFailureFixture : ParseFailureFixture {
        fun failAt(offset: Int, line: Int = 1, col: Int = offset + 1)

        fun expectContext(textBeforeFailure: String, textAfterFailure: String)

        fun expectOneChar() {
            expect("any character")
        }

        fun expectOneInRange(from: Char, to: Char)

        fun expectLiteral(text: String)
    }

    private class DefaultTextParseFailureFixture : DefaultParseFailureFixture(), TextParseFailureFixture {
        var offset = 0
        var line = 1
        var col = 1
        var textBefore: String? = null
        var textAfter: String? = null

        override fun expectLiteral(text: String) {
            expect.expectLiteral(text)
        }

        override fun expectOneInRange(from: Char, to: Char) {
            expect.expectRange(from, to)
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

    interface BinaryParseFailureFixture : ParseFailureFixture {
        fun failAt(offset: Int)

        fun expectLiteral(byte: Byte)

        fun expectOneInRange(from: Byte, to: Byte)

        fun expectContext(context: String)
    }

    private class DefaultBinaryParseFailureFixture : DefaultParseFailureFixture(), BinaryParseFailureFixture {
        var offset = 0
        private var context: String? = null

        override fun expectLiteral(byte: Byte) {
            expect.expectLiteral(byte)
        }

        override fun expectOneInRange(from: Byte, to: Byte) {
            expect.expectRange(from, to)
        }

        override fun failAt(offset: Int) {
            this.offset = offset
        }

        override fun expectContext(context: String) {
            this.context = context
        }

        fun failureContext(input: ByteArray): String {
            return if (context != null) {
                context!!
            } else if (offset >= input.size) {
                "end of input"
            } else {
                format(input[offset])
            }
        }
    }
}