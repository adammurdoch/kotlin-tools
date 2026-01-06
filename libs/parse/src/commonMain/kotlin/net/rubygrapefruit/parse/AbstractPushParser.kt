package net.rubygrapefruit.parse

internal abstract class AbstractPushParser<IN, OUT>(
    parser: PullParser<IN, OUT>
) : PushParser<OUT> {
    private var state: PullParser.Result<IN, OUT> = PullParser.RequireMore(parser)

    protected fun inputAvailable(input: IN) {
        val currentState = state
        when (currentState) {
            is PullParser.Matched -> state = PullParser.Failed(currentState.count)
            is PullParser.Failed -> {}
            is PullParser.RequireMore -> state = currentState.parser.parse(input)
        }
    }

    override fun endOfInput(): ParseResult<OUT> {
        val currentState = state
        return when (currentState) {
            is PullParser.Matched -> ParseResult.Success(currentState.value)
            is PullParser.Failed -> ParseResult.Fail()
            is PullParser.RequireMore -> ParseResult.Fail()
        }
    }
}