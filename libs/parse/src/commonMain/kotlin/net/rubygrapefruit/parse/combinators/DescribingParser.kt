package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class DescribingParser<IN, OUT>(
    private val parser: Parser<IN, OUT>,
    private val description: String
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return DescribingParser(DiscardParser(parser), description)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return DescribingCompiledParser(compiler.compile(parser), Expectation.One(description))
    }


    internal class DescribingCompiledParser<IN, OUT>(
        val parser: CompiledParser<IN, OUT>,
        private val expectation: Expectation
    ) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return DescribingPullParser(parser.start(next), expectation)
        }
    }

    private class DescribingPullParser<IN, NEXT>(
        private var parser: PullParser<IN, NEXT>,
        private val expectation: Expectation
    ) : PullParser<IN, NEXT> {
        private var advanced = 0

        override fun stop(): PullParser.Failed {
            val failure = parser.stop()
            return mapFailure(failure)
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, NEXT> {
            val result = parser.parse(input, max)
            return when (result) {
                is PullParser.Matched -> result
                is PullParser.Failed -> mapFailure(result)

                is PullParser.RequireMore -> {
                    advanced += result.advance
                    parser = result.parser
                    PullParser.RequireMore(result.advance, result.matched, this, result.failedChoice)
                }
            }
        }

        private fun mapFailure(result: PullParser.Failed): PullParser.Failed = if (result.index == -advanced) {
            PullParser.Failed(result.index, expectation)
        } else {
            result
        }
    }
}

/**
 * Returns a parser that applies the given parser and uses the given description in error messages.
 */
fun <IN, OUT> describedAs(parser: Parser<IN, OUT>, description: String): Parser<IN, OUT> {
    return DescribingParser(parser, description)
}