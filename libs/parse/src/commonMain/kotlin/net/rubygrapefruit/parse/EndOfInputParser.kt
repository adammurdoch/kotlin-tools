package net.rubygrapefruit.parse

internal class EndOfInputParser<IN : Input<*>, OUT>(
    private val result: PullParser.Matched<IN, OUT>
) : PullParser<IN, OUT> {
    override fun parse(input: IN): PullParser.Result<IN, OUT> {
        return PullParser.Failed(result.count, listOf("end of input"))
    }

    override fun endOfInput(input: IN): PullParser.Finished<IN, OUT> {
        return if (input.length > result.count) {
            PullParser.Failed(result.count, listOf("end of input"))
        } else {
            result
        }
    }
}