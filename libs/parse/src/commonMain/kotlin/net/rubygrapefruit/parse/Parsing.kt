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
        is PullParser.Failed -> ParseResult.Fail(input.posAt(result.index), "Expected ${result.expected.joinToString(", ")}")
    }
}

internal fun <IN : Input<*>, OUT> Parser<*, OUT>.compile(): PullParser<IN, OUT> {
    return DefaultConverter<IN>().convert(this) { match -> PullParser.RequireMore(match.count, EndOfInputParser(match.value)) }
}

private class DefaultConverter<IN : Input<*>> : CombinatorBuilder.Converter<IN> {
    override fun <OUT> builder(parser: Parser<*, OUT>): ParserBuilder<IN, OUT> {
        return when (parser) {
            is ParserBuilder<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                (parser as ParserBuilder<IN, OUT>)
            }

            else -> {
                object : ParserBuilder<IN, OUT> {
                    override fun <NEXT> build(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
                        return convert(parser, next)
                    }
                }
            }
        }
    }

    override fun <OUT> convert(parser: Parser<*, OUT>): PullParser<IN, OUT> {
        return convert(parser, ParseContinuation.of())
    }

    override fun <OUT, NEXT> convert(parser: Parser<*, OUT>, next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>): PullParser<IN, NEXT> {
        return convert(parser, ParseContinuation.of(next))
    }

    override fun <OUT, NEXT> convert(parser: Parser<*, OUT>, next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
        return when (parser) {
            is ParserBuilder<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                (parser as ParserBuilder<IN, OUT>).build(next)
            }

            is CombinatorBuilder<*> -> {
                @Suppress("UNCHECKED_CAST")
                (parser as CombinatorBuilder<OUT>).build(this, next)
            }

            else -> throw IllegalArgumentException("Cannot compile parser $parser with unexpected type")
        }
    }
}
