package net.rubygrapefruit.parse

internal open class DefaultPushParser<CONTEXT, IN : AdvancingInput<*>, OUT>(
    parser: PullParser<IN, OUT>
) {
    private var state: ParseState<IN, OUT> = parser
    private var failedChoice: ExpectationProvider? = null
    private var failedChoiceIndex = 0

    fun inputAvailable(input: IN) {
        while (true) {
            val currentState = state
            when (currentState) {
                is PullParser.Finished -> return
                is PullParser -> {
                    val result = currentState.parse(input, input.available)
                    when (result) {
                        is PullParser.Failed -> {
                            state = if (failedChoice != null) {
                                PullParser.Failed.merged(listOf(PullParser.Failed(failedChoiceIndex, failedChoice!!), result))
                            } else {
                                result
                            }
                        }

                        is PullParser.Matched -> state = result
                        is PullParser.RequireMore -> {
                            input.advance(result.advance)
                            if (result.failedChoice != null) {
                                if (result.advance == 0 && failedChoice != null) {
                                    failedChoice = ExpectationProvider.oneOf(failedChoice!!, result.failedChoice)
                                } else {
                                    failedChoice = result.failedChoice
                                    failedChoiceIndex = 0
                                }
                            } else {
                                failedChoiceIndex -= result.advance
                            }
                            state = result.parser
                            if (input.available == 0 && !input.finished) {
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
            is PullParser.Failed -> failureFactory(input, result.index, result.expected.expectation().format())
            is PullParser -> throw IllegalStateException("Expected parsing to be finished, but is $result")
        }
    }
}