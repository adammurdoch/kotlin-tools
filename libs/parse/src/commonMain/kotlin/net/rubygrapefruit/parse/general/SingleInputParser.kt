package net.rubygrapefruit.parse.general

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.combinators.Extractor
import net.rubygrapefruit.parse.combinators.NextValueExtractor
import net.rubygrapefruit.parse.combinators.UnitExtractor
import net.rubygrapefruit.parse.stream.BoxingInput

internal class SingleInputParser<IN, ITEM, OUT, STREAM : BoxingInput<*, ITEM>>(
    val predicate: InputPredicate<STREAM>,
    private val extractor: Extractor<STREAM, OUT> = NextValueExtractor.of()
) : Parser<IN, OUT>, TypedInputCombinatorBuilder<STREAM, OUT>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return SingleInputParser(predicate, UnitExtractor)
    }

    override fun compile(compiler: CombinatorBuilder.Compiler<STREAM>): CompiledParser<STREAM, OUT> {
        return SingleInputCompiledParser(predicate, extractor)
    }
}