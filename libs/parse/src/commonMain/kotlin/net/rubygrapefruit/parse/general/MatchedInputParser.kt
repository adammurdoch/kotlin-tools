package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.combinators.DiscardParser

internal class MatchedInputParser<IN, OUT>(
    private val parser: Parser<IN, *>
) : Parser<IN, OUT>, TypedInputCombinatorBuilder<SlicingInput<OUT>, OUT>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return DiscardParser(parser)
    }

    override fun compile(compiler: CombinatorBuilder.Compiler<SlicingInput<OUT>>): CompiledParser<SlicingInput<OUT>, OUT> {
        return MatchedInputCompiledParser(compiler.compile(parser))
    }

    internal class MatchedInputCompiledParser<OUT>(val parser: CompiledParser<SlicingInput<OUT>, *>) : CompiledParser<SlicingInput<OUT>, OUT> {
        override fun <NEXT> start(next: ParseContinuation<SlicingInput<OUT>, OUT, NEXT>): PullParser<SlicingInput<OUT>, NEXT> {
            return parser.start { length, _ ->
                CollectMatchedInputPullParser(length, next)
            }
        }
    }

    private class CollectMatchedInputPullParser<OUT, NEXT>(
        private val length: Int,
        private val next: ParseContinuation<SlicingInput<OUT>, OUT, NEXT>
    ) : PullParser<SlicingInput<OUT>, NEXT> {
        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun toString(): String {
            return "{collect-matched-input length=$length}"
        }

        override fun stop(): PullParser.Failed {
            return PullParser.Failed(0 ,expectation)
        }

        override fun parse(input: SlicingInput<OUT>, max: Int): PullParser.Result<SlicingInput<OUT>, NEXT> {
            return next.matched(-length, 0, input.get(-length, 0))
        }
    }
}