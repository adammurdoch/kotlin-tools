package net.rubygrapefruit.parse

internal fun <POS, IN : Input<POS>, OUT> parse(parser: PullParser<IN, OUT>, input: IN): ParseResult<POS, OUT> {
    var current = parser
    while (true) {
        val result = current.parse(input)
        when (result) {
            is PullParser.Finished -> return finalResult(result, input)
            is PullParser.RequireMore -> current = result.parser
        }
    }
}

internal fun <POS, IN : Input<POS>, OUT> finalResult(result: PullParser.Result<IN, OUT>, input: IN): ParseResult<POS, OUT> {
    return when (result) {
        is PullParser.Matched -> ParseResult.Success(result.value)
        is PullParser.Failed -> ParseResult.Fail(input.posAt(result.index), "Expected ${result.expected.joinToString(", ")}")
        is PullParser.RequireMore -> throw IllegalArgumentException("Expected parsing to be finished")
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
