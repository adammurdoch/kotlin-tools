package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.CombinatorBuilder
import net.rubygrapefruit.parse.CompiledParser
import net.rubygrapefruit.parse.DiscardableParser
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.stream.Input

internal class OneOrMoreProduceNothingParser<IN>(
    private val item: Parser<IN, Unit>,
    private val separator: Parser<IN, Unit>?
) : Parser<IN, Unit>, CombinatorBuilder<Unit>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return this
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        if (separator == null) {
            val singleInputParser = compiler.maybeAsSingleInputParser(item)
            if (singleInputParser != null) {
                return RepeatingSingleInputCompiledParser(1, singleInputParser.predicate, singleInputParser.expectation, UnitRangeAccumulator)
            }
        }
        return OneOrMoreParser.of(item, separator, compiler, UnitAccumulator)
    }
}