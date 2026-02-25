package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class ZeroOrMoreProduceNothingParser<IN>(
    private val parser: Parser<IN, Unit>,
    private val separator: Parser<IN, Unit>?
) : Parser<IN, Unit>, CombinatorBuilder<Unit>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return this
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        if (separator == null) {
            val singleValueOption = compiler.maybeAsSingleInputParser(parser)
            if (singleValueOption != null) {
                return ZeroOrMoreSingleInputCompiledParser(singleValueOption, UnitRangeAccumulator)
            }
        }
        val option = compiler.compile(parser)
        val tail = if (separator == null) {
            option
        } else {
            val tail = Sequence2Parser(separator, parser) { _, _ -> }
            compiler.compile(tail)
        }
        return ZeroOrMoreParser.of(option, tail, UnitAccumulator.Empty)
    }
}