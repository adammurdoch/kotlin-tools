package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*

internal class MatchedInputParser<IN, OUT>(
    private val parser: Parser<IN, Unit>
) : Parser<IN, OUT>, TypedInputCombinatorBuilder<SlicingInput<OUT>, OUT>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return parser
    }

    override fun compile(compiler: CombinatorBuilder.Compiler<SlicingInput<OUT>>): CompiledParser<SlicingInput<OUT>, OUT> {
        return MatchedInputCompiledParser(compiler.compile(parser))
    }

    internal class MatchedInputCompiledParser<OUT>(val parser: CompiledParser<SlicingInput<OUT>, Unit>) : CompiledParser<SlicingInput<OUT>, OUT> {
        override fun <NEXT> start(next: ParseContinuation<SlicingInput<OUT>, OUT, NEXT>): PullParser<SlicingInput<OUT>, NEXT> {
            return parser.start(CollectingContinuation(next))
        }
    }

    private class CollectingContinuation<OUT, NEXT>(val next: ParseContinuation<SlicingInput<OUT>, OUT, NEXT>) : ParseContinuation<SlicingInput<OUT>, Unit, NEXT> {
        override fun matched(start: Int, end: Int, value: ValueProvider<Unit>): PullParser.RequireMore<SlicingInput<OUT>, NEXT> {
            return PullParser.RequireMore(end, false, next(end - start, value))
        }

        override fun next(length: Int, value: ValueProvider<Unit>): PullParser<SlicingInput<OUT>, NEXT> {
            return CollectMatchedInputPullParser(length, next)
        }
    }

    private class CollectMatchedInputPullParser<OUT, NEXT>(
        private val length: Int,
        private val next: ParseContinuation<SlicingInput<OUT>, OUT, NEXT>
    ) : PullParser<SlicingInput<OUT>, NEXT> {
        override fun toString(): String {
            return "{collect-matched-input length=$length}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed(0, Expectation.Nothing)
        }

        override fun parse(input: SlicingInput<OUT>, max: Int): PullParser.Result<SlicingInput<OUT>, NEXT> {
            return next.matched(-length, 0, input.get(-length, 0))
        }
    }
}