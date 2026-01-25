package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class DiscardParser<IN>(private val parser: Parser<IN, *>) : Parser<IN, Unit>, CombinatorBuilder<Unit>, CombinatorSingleInputBuilder, DiscardableParser<IN> {
    override fun withNoResult(): Parser<IN, Unit> {
        return this
    }

    override fun <IN> maybeAsSingleInputParser(compiler: CombinatorSingleInputBuilder.Compiler<IN>): SingleInputParser<IN>? {
        return compiler.maybeAsSingleInputParser(parser)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return compiler.compileWithNoResult(parser)
    }
}

