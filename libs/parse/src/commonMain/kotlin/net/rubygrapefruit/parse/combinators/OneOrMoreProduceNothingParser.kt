package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class OneOrMoreProduceNothingParser<IN>(
    private val parser: Parser<IN, Unit>
) : Parser<IN, Unit>, CombinatorBuilder<Unit>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return this
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return OneOrMoreParser.of(compiler.compile(parser), UnitAccumulator.Empty)
    }
}