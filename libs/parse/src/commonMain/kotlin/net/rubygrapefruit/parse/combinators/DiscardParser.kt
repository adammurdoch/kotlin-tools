package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class DiscardParser<IN>(private val parser: Parser<IN, *>) : Parser<IN, Unit>, CombinatorBuilder<Unit>, CombinatorSingleInputBuilder<IN> {
    override fun withNoResult(): CombinatorBuilder<Unit> {
        return this
    }

    override fun maybeAsSingleInputParser(): SingleInputParser<IN>? {
        return parser as? SingleInputParser<IN>
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return compiler.compileWithNoResult(parser)
    }
}

