package net.rubygrapefruit.parse

internal fun <POS, IN : AdvancingInput<POS>, OUT> parse(parser: PullParser<IN, OUT>, input: IN): ParseResult<POS, OUT> {
    var current = parser
    while (true) {
        val result = current.parse(input, input.available)
        when (result) {
            is PullParser.Finished -> return finalResult(result, input)
            is PullParser.RequireMore -> {
                if (result.advance == 0 && result.parser == current) {
                    throw IllegalStateException("Parsing cannot proceed")
                }
                input.advance(result.advance)
                current = result.parser
            }
        }
    }
}

internal fun <POS, IN : Input<POS>, OUT> finalResult(result: PullParser.Finished<IN, OUT>, input: IN): ParseResult<POS, OUT> {
    return when (result) {
        is PullParser.Matched -> ParseResult.Success(result.value)
        is PullParser.Failed -> {
            ParseResult.Fail(input.posAt(result.index), result.expected.format())
        }
    }
}

internal fun Expectation.format(): String {
    val expected = mutableSetOf<String>()
    accept(expected::add)
    return "Expected ${expected.sorted().joinToString(", ")}"
}

internal fun <IN, OUT> PullParser<IN, OUT>.parseZeroOrOne(input: IN, maxAdvance: Int): PullParser.Result<IN, OUT> {
    var current = this
    while (true) {
        val result = current.parse(input, maxAdvance)
        if (maxAdvance == 1 && result is PullParser.RequireMore && result.advance == 0) {
            current = result.parser
            continue
        }
        return result
    }
}

internal fun <IN : Input<*>, OUT> Parser<*, OUT>.start(): PullParser<IN, OUT> {
    return DefaultCompiler<IN>().compile(this).start { match -> PullParser.RequireMore(match.count, EndOfInputParser(match.value)) }
}

internal fun <IN : Input<*>, OUT> Parser<*, OUT>.compile(): CompiledParser<IN, OUT> {
    return DefaultCompiler<IN>().compile(this)
}

private class DefaultCompiler<IN : Input<*>> : CombinatorBuilder.Compiler<IN> {
    private val compiledParsers = mutableMapOf<Parser<*, *>, CompiledParser<IN, *>>()

    override fun <OUT> compile(parser: Parser<*, OUT>): CompiledParser<IN, OUT> {
        val compiled = compiledParsers.getOrPut(parser) { doCompile(parser) }
        @Suppress("UNCHECKED_CAST")
        return compiled as CompiledParser<IN, OUT>
    }

    private fun <OUT> doCompile(parser: Parser<*, OUT>): CompiledParser<IN, OUT> {
        return when (parser) {
            is ParserBuilder<*, *> -> {
                object : CompiledParser<IN, OUT> {
                    override val mayNotAdvanceOnMatch: Boolean
                        get() = false

                    override val expectation: Expectation
                        get() = parser.expectation

                    override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
                        @Suppress("UNCHECKED_CAST")
                        return (parser as ParserBuilder<IN, OUT>).start(next)
                    }
                }
            }

            is CombinatorBuilder<*> -> {
                @Suppress("UNCHECKED_CAST")
                (parser as CombinatorBuilder<OUT>).compile(this)
            }

            else -> throw IllegalArgumentException("Cannot compile parser $parser with unexpected type")
        }
    }
}
