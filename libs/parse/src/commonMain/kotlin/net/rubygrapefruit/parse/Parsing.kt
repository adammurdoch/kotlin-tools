package net.rubygrapefruit.parse

internal fun <POS, IN : Input<POS>, OUT> parse(parser: PullParser<IN, OUT>, input: IN): ParseResult<POS, OUT> {
    val result = parser.parse(input)
    return finalResult(result, input)
}

internal fun <POS, IN : Input<POS>, OUT> finalResult(result: PullParser.Result<IN, OUT>, input: IN): ParseResult<POS, OUT> {
    val finalResult = when (result) {
        is PullParser.Matched -> result
        is PullParser.Failed -> result
        is PullParser.RequireMore -> result.parser.endOfInput(input)
    }
    return when (finalResult) {
        is PullParser.Matched -> ParseResult.Success(finalResult.value)
        is PullParser.Failed -> ParseResult.Fail(input.posAt(finalResult.index), "Expected ${finalResult.expected.joinToString(", ")}")
    }
}

internal fun <IN : Input<*>, OUT> Parser<*, OUT>.compile(): PullParser<IN, OUT> {
    return DefaultConverter<IN>().convert(this) { match -> PullParser.RequireMore(EndOfInputParser(match)) }
}

private class DefaultConverter<IN : Input<*>> : CombinatorBuilder.Converter<IN> {
    override fun <OUT, NEXT> convert(parser: Parser<*, OUT>, next: (PullParser.Matched<IN, OUT>) -> PullParser.Result<IN, NEXT>): PullParser<IN, NEXT> {
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
