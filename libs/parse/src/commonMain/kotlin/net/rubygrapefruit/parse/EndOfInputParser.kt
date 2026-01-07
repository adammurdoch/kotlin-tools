package net.rubygrapefruit.parse

internal class EndOfInputParser<IN : Input<*>, OUT>(
    private val result: PullParser.Matched<IN, OUT>
) : PullParser<IN, OUT> {
    override fun parse(input: IN): PullParser.Result<IN, OUT> {
        return if (input.available > 0) {
            PullParser.Failed(0, listOf("end of input"))
        } else if (input.finished) {
            result
        } else {
            PullParser.RequireMore(0, this)
        }
    }
}