package net.rubygrapefruit.parse

internal abstract class AbstractPushParser<POS, IN : Input<POS>, OUT>(
    parser: PullParser<IN, OUT>
) : PushParser<POS, OUT> {
    private var state: PullParser.Result<IN, OUT> = PullParser.RequireMore(parser)

    protected fun inputAvailable(input: IN) {
        val currentState = state
        when (currentState) {
            is PullParser.Matched -> state = PullParser.Failed(currentState.count, listOf("expected end of input"))
            is PullParser.Failed -> {}
            is PullParser.RequireMore -> state = currentState.parser.parse(input)
        }
    }

    protected fun endOfInput(input: IN): ParseResult<POS, OUT> {
        inputAvailable(input)
        return finalResult(state, input)
    }
}