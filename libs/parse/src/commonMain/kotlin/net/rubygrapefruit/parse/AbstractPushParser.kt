package net.rubygrapefruit.parse

internal abstract class AbstractPushParser<POS, IN : AdvancingInput<POS>, OUT>(
    parser: PullParser<IN, OUT>
) : PushParser<POS, OUT> {
    private var state: ParseState<IN, OUT> = parser

    protected fun inputAvailable(input: IN) {
        while (true) {
            val currentState = state
            when (currentState) {
                is PullParser.Finished -> return
                is PullParser -> {
                    val result = currentState.parse(input)
                    when (result) {
                        is PullParser.Finished -> state = result
                        is PullParser.RequireMore -> {
                            input.advance(result.advance)
                            state = result.parser
                            if (!input.finished) {
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    protected fun endOfInput(input: IN): ParseResult<POS, OUT> {
        inputAvailable(input)

        val currentState = state
        return when (currentState) {
            is PullParser.Finished -> finalResult(currentState, input)
            is PullParser -> throw IllegalStateException("Expected parsing to be finished, but is $currentState")
        }
    }
}