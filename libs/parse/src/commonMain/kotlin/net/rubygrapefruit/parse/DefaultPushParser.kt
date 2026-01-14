package net.rubygrapefruit.parse

internal open class DefaultPushParser<POS, IN : AdvancingInput<POS>, OUT>(
    parser: PullParser<IN, OUT>
) {
    private var state: ParseState<IN, OUT> = parser
    private var expected: Expectation = parser.expected ?: Expectation.Nothing
    private var expectedPos = 0

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
                            val newExpected = result.parser.expected
                            if (newExpected != null) {
                                expected = newExpected
                                expectedPos = 0
                            } else {
                                expectedPos--
                            }
                            if (!input.finished) {
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    fun endOfInput(input: IN): ParseResult<POS, OUT> {
        inputAvailable(input)

        val currentState = state
        return when (currentState) {
            is PullParser.Finished -> finalResult(currentState, expected, expectedPos, input)
            is PullParser -> throw IllegalStateException("Expected parsing to be finished, but is $currentState")
        }
    }

    private fun finalResult(
        result: PullParser.Finished<IN, OUT>,
        expected: Expectation,
        expectedPos: Int,
        input: IN
    ): ParseResult<POS, OUT> {
        return when (result) {
            is PullParser.Matched -> ParseResult.Success(result.value)
            is PullParser.Failed -> {
                ParseResult.Fail(input.posAt(expectedPos), expected.format())
            }
        }
    }
}