package net.rubygrapefruit.parse

internal fun <IN : Input, OUT> parse(parser: PullParser<IN, OUT>, input: IN): ParseResult<OUT> {
    val result = parser.parse(input)
    return when (result) {
        is PullParser.Matched -> if (result.count == input.length) {
            ParseResult.Success(result.value)
        } else {
            ParseResult.Fail()
        }

        is PullParser.Failed -> ParseResult.Fail()
        is PullParser.RequireMore -> ParseResult.Fail()
    }
}