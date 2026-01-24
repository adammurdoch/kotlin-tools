package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.CombinatorBuilder
import net.rubygrapefruit.parse.CompiledParser
import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.ZeroOrMoreParser.Companion.of

internal class ZeroOrMoreProduceNothingParser<IN>(private val parser: Parser<IN, Unit>) : Parser<IN, Unit>, CombinatorBuilder<Unit> {
    override fun withNoResult(): CombinatorBuilder<Unit> {
        TODO()
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        val singleValueOption = compiler.maybeAsSingleInputParser(parser)
        return if (singleValueOption != null) {
            ZeroOrMoreSingleInputCompiledParser(singleValueOption, UnitRangeAccumulator)
        } else {
            val option = compiler.compile(parser)
            of(option, UnitAccumulator.Empty)
        }
    }
}