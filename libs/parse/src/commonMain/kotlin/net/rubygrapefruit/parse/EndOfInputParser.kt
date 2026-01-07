package net.rubygrapefruit.parse

internal class EndOfInputParser<IN, OUT>(
    private val result: PullParser.Matched<IN, OUT>
) : PullParser<IN, OUT> {
    override fun parse(input: IN): PullParser.Result<IN, OUT> {
        return PullParser.Failed(result.count, listOf("end of input"))
    }

    override fun endOfInput(input: IN): PullParser.Finished<IN, OUT> {
        return result
    }
}