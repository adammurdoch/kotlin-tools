package net.rubygrapefruit.parse

internal class EndOfInputParser<IN : Input<*>, OUT>(
    private val result: PullParser.Matched<IN, OUT>
) : PullParser<IN, OUT> {
    override fun parse(input: IN): PullParser.Result<IN, OUT> {
        return if (input.available > result.count) {
            PullParser.Failed(result.count, listOf("end of input"))
        } else if (input.finished) {
            result
        } else {
            PullParser.RequireMore(this)
        }
    }
}