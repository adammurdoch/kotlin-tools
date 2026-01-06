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
        is PullParser.Matched -> if (finalResult.count == input.length) {
            ParseResult.Success(finalResult.value)
        } else {
            ParseResult.Fail(input.posAt(finalResult.count), "Expected end of input")
        }

        is PullParser.Failed -> ParseResult.Fail(input.posAt(finalResult.index), "Expected ${finalResult.expected.joinToString(", ")}")
    }
}

internal fun <IN : Input<*>, OUT> Parser<*, OUT>.compile(): PullParser<IN, OUT> {
    return DefaultConverter<IN, OUT>().convert(this)
}

private class DefaultConverter<IN : Input<*>, OUT> : ParserBuilder.Converter<IN, OUT> {
    override fun convert(parser: Parser<*, OUT>): PullParser<IN, OUT> {
        return when (parser) {
            is PullParser<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                parser as PullParser<IN, OUT>
            }

            is ParserBuilder<*> -> {
                @Suppress("UNCHECKED_CAST")
                (parser as ParserBuilder<OUT>).build(this)
            }

            else -> throw IllegalArgumentException("Cannot compile parser $parser with unexpected type")
        }
    }
}
