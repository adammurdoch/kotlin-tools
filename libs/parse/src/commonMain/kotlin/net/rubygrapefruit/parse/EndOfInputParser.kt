package net.rubygrapefruit.parse

internal class EndOfInputParser<IN : Input<*>, OUT>(
    private val result: OUT
) : PullParser<IN, OUT> {
    override val expected: Expectation = Expectation.One("end of input")

    override fun toString(): String {
        return "{end-of-input}"
    }

    override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
        return if (input.available > 0) {
            PullParser.Failed(0, Expectation.One("end of input"))
        } else if (input.finished) {
            PullParser.Matched(0, result)
        } else {
            PullParser.RequireMore(0, this)
        }
    }
}