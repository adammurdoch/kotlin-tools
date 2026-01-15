package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.CombinatorBuilder.Compiler

internal interface TypedInputCombinatorBuilder<IN : Input<*>, OUT> {
    fun compile(compiler: Compiler<IN>): CompiledParser<IN, OUT>
}