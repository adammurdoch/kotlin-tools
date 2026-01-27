package net.rubygrapefruit.parse

internal open class DefaultPushParser<CONTEXT, IN : AdvancingInput<*>, OUT>(
    parser: PullParser<IN, OUT>
) {
    private var state: ParseState<IN, OUT> = parser

    fun inputAvailable(input: IN) {
        while (true) {
            val currentState = state
            when (currentState) {
                is PullParser.Finished -> return
                is PullParser -> {
                    val result = currentState.parse(input, input.available)
                    when (result) {
                        is PullParser.Finished -> state = result
                        is PullParser.RequireMore -> {
                            input.advance(result.advance)
                            state = result.parser
                            if (!input.finished) {
                                return
                            }
                        }
                    }
                }
            }
        }
    }

    fun endOfInput(input: IN, failureFactory: (IN, Int, String) -> ParseResult.Fail<CONTEXT>): ParseResult<CONTEXT, OUT> {
        inputAvailable(input)

        val result = state
        return when (result) {
            is PullParser.Matched -> ParseResult.Success(result.value)
            is PullParser.Failed -> failureFactory(input, result.index, result.expected.format())
            is PullParser -> throw IllegalStateException("Expected parsing to be finished, but is $result")
        }
    }
}