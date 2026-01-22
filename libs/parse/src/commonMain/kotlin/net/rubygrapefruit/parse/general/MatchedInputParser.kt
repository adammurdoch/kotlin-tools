package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.CombinatorBuilder
import net.rubygrapefruit.parse.CompiledParser
import net.rubygrapefruit.parse.Expectation
import net.rubygrapefruit.parse.ParseContinuation
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.PullParser
import net.rubygrapefruit.parse.SlicingInput
import net.rubygrapefruit.parse.TypedInputCombinatorBuilder

internal class MatchedInputParser<IN, OUT>(private val parser: Parser<IN, *>) : Parser<IN, OUT>, TypedInputCombinatorBuilder<SlicingInput<OUT>, OUT> {
    override fun compile(compiler: CombinatorBuilder.Compiler<SlicingInput<OUT>>): CompiledParser<SlicingInput<OUT>, OUT> {
        return MatchedInputCompiledParser(compiler.compile(parser))
    }

    private class MatchedInputCompiledParser<OUT>(private val parser: CompiledParser<SlicingInput<OUT>, *>) : CompiledParser<SlicingInput<OUT>, OUT> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = parser.mayNotAdvanceOnMatch

        override val expectation: Expectation
            get() = parser.expectation

        override fun <NEXT> start(next: ParseContinuation<SlicingInput<OUT>, OUT, NEXT>): PullParser<SlicingInput<OUT>, NEXT> {
            return parser.start { matched ->
                PullParser.RequireMore(matched.end, CollectMatchedInputPullParser(matched.end - matched.start, next))
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

        override fun parse(input: SlicingInput<OUT>, max: Int): PullParser.Result<SlicingInput<OUT>, NEXT> {
            return next.matched(-length, 0, input.get(-length, 0))
        }
    }
}