package net.rubygrapefruit.parse

internal open class DefaultPushParser<CONTEXT, IN : ContextualInput<CONTEXT, *>, OUT>(
    parser: PullParser<IN, OUT>,
    private val failureFormatter: (CONTEXT, String) -> String
) {
    private var state: ParseState<IN, OUT> = parser
    private var failedChoice: ExpectationProvider? = null
    private var failedChoiceIndex = 0
    private var failure: ParseResult.Fail<CONTEXT>? = null

    fun inputAvailable(input: IN): ParseResult.Fail<CONTEXT>? {
        while (true) {
            val currentState = state
            when (currentState) {
                is PullParser.Matched -> return null
                is PullParser.Failed -> return failure!!

                is PullParser -> {
                    val result = currentState.parse(input, input.available)
                    when (result) {
                        is PullParser.Failed -> {
                            val effectiveFailure = if (failedChoice != null) {
                                PullParser.Failed.merged(listOf(PullParser.Failed(failedChoiceIndex, failedChoice!!), result))
                            } else {
                                result
                            }
                            failure = mapFailed(input, effectiveFailure)
                            state = effectiveFailure
                        }

                        is PullParser.Matched -> state = result
                        is PullParser.RequireMore -> {
                            input.advance(result.advance)
                            if (result.failedChoice != null) {
                                if (result.advance == 0 && failedChoiceIndex == 0 && failedChoice != null) {
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
                                return null
                            }
                        }
                    }
                }
            }
        }
    }

    fun endOfInput(input: IN): ParseResult<CONTEXT, OUT> {
        inputAvailable(input)

        val result = state
        return when (result) {
            is PullParser.Matched -> ParseResult.Success(result.value)
            is PullParser.Failed -> failure!!
            is PullParser -> throw IllegalStateException("Expected parsing to be finished, but is $result")
        }
    }

    fun maybeFailed(): ParseResult.Fail<CONTEXT>? {
        return failure
    }

    private fun mapFailed(input: IN, result: PullParser.Failed): ParseResult.Fail<CONTEXT> {
        val context = input.contextAt(result.index)
        return ParseResult.Fail(context, result.expected.expectation().format(), failureFormatter)
    }
}