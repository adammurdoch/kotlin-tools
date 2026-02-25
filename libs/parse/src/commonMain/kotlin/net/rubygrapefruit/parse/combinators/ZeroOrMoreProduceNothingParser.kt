package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.combinators.ZeroOrMoreParser.Companion.of

internal class ZeroOrMoreProduceNothingParser<IN>(
    private val parser: Parser<IN, Unit>,
    private val separator: Parser<IN, Unit>?
) : Parser<IN, Unit>, CombinatorBuilder<Unit>, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return this
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return if (separator == null) {
            val singleValueOption = compiler.maybeAsSingleInputParser(parser)
            if (singleValueOption != null) {
                ZeroOrMoreSingleInputCompiledParser(singleValueOption, UnitRangeAccumulator)
            } else {
                val option = compiler.compile(parser)
                of(option, UnitAccumulator.Empty)
            }
        } else {
            val option = compiler.compile(parser)
            val tail = Sequence2Parser(separator, parser) { _, _ -> }
            val compiledTail = compiler.compile(tail)
            of(option, compiledTail, UnitAccumulator.Empty)
        }
    }
}