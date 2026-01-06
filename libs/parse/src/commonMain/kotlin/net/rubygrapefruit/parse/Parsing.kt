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
            ParseResult.Fail(input.posAt(finalResult.count), "expected end of input")
        }

        is PullParser.Failed -> ParseResult.Fail(input.posAt(finalResult.index), finalResult.message)
    }
}