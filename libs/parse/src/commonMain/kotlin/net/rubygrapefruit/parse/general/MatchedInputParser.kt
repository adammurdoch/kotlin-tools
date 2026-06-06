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
        override fun start(start: Position, next: ParseContinuation<SlicingInput<OUT>, OUT>): PullParser<SlicingInput<OUT>> {
            return parser.start(start, CollectMatchedInputParseContinuation(next))
        }
    }

    private class CollectMatchedInputParseContinuation<OUT>(
        private val next: ParseContinuation<SlicingInput<OUT>, OUT>
    ) : ParseContinuation<SlicingInput<OUT>, Unit> {
        override fun matched(
            input: SlicingInput<OUT>,
            advance: Int,
            length: Int,
            value: ValueProvider<Unit>,
            failedChoices: List<PullParser.Failure>
        ): PullParser.Result<SlicingInput<OUT>> {
            val slice = input.get(advance - length, advance)
            return next.matched(input, advance, length, ValueProvider.of(slice), failedChoices)
        }

        override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.Continuing<T> {
            return next.selected(advance, parser, failedChoices)
        }
    }
}