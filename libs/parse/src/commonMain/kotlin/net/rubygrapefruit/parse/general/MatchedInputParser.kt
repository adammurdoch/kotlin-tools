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
        next: ParseContinuation<SlicingInput<OUT>, OUT>
    ) : ParseContinuation.MappingParseContinuation<SlicingInput<OUT>, Unit, OUT>(next) {
        override fun map(input: SlicingInput<OUT>, start: Int, end: Int, value: ValueProvider<Unit>): ValueProvider<OUT> {
            val slice = input.get(start, end)
            return ValueProvider.of(slice)
        }
    }
}