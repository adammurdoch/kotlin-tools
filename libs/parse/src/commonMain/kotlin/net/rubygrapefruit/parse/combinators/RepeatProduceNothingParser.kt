package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.CombinatorBuilder
import net.rubygrapefruit.parse.CompiledParser
import net.rubygrapefruit.parse.DiscardableParser
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.general.SucceedParser
import net.rubygrapefruit.parse.stream.Input

internal class RepeatProduceNothingParser<IN>(
    private val count: Int,
    private val item: Parser<IN, Unit>,
    private val separator: Parser<IN, Unit>?
) : Parser<IN, Unit>, CombinatorBuilder<Unit>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return this
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return if (count == 0) {
            SucceedParser.SucceedCompiledParser(UnitAccumulator)
        } else {
            rangeParser(Range.Exact(count), item, separator, compiler)
        }
    }
}