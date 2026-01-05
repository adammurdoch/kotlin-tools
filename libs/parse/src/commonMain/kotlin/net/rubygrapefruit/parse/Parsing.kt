package net.rubygrapefruit.parse

internal fun <IN : Input, OUT> parse(parser: ConsumingParser<IN, OUT>, input: IN): ParseResult<OUT> {
    val result = parser.parse(input)
    return when (result) {
        is ConsumingParser.Result.Success -> if (result.count == input.length) {
            ParseResult.Success(result.value)
        } else {
            ParseResult.Fail()
        }

        is ConsumingParser.Result.Fail -> ParseResult.Fail()
    }
}