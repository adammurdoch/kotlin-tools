package net.rubygrapefruit.parse

internal abstract class AbstractPushParser<POS, IN : AdvancingInput<POS>, OUT>(
    parser: PullParser<IN, OUT>
) : PushParser<POS, OUT> {
    private var state: ParseState<IN, OUT> = parser

    protected fun inputAvailable(input: IN) {
        val currentState = state
        when (currentState) {
            is PullParser.Finished -> {}
            is PullParser -> {
                val result = currentState.parse(input)
                when (result) {
                    is PullParser.Finished -> state = result
                    is PullParser.RequireMore -> {
                        input.advance(result.advance)
                        state = result.parser
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
            is PullParser -> throw IllegalStateException("Expected parsing to be finished")
        }
    }
}