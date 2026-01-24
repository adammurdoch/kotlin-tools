package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.CombinatorBuilder
import net.rubygrapefruit.parse.CompiledParser
import net.rubygrapefruit.parse.Input
import net.rubygrapefruit.parse.Parser

internal class DiscardParser<IN>(private val parser: Parser<IN, *>) : Parser<IN, Unit>, CombinatorBuilder<Unit> {
    override fun withNoResult(): CombinatorBuilder<Unit> {
        TODO()
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, Unit> {
        return compiler.compileWithNoResult(parser)
    }
}

