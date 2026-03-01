package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.CombinatorBuilder
import net.rubygrapefruit.parse.CompiledParser
import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.Parser

internal class RepeatProduceNothingParser<IN>(
    private val count: Int,
    private val parser: Parser<IN, Unit>
) : Parser<IN, Unit>, CombinatorBuilder<Unit> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return RepeatParser.of(count, compiler.compile(parser), UnitAccumulator.Empty)
    }
}