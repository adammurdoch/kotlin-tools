package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.CombinatorBuilder.Compiler

internal interface TypedInputCombinatorBuilder<IN, OUT> {
    /**
     * Creates a copy of this parser that produces no result.
     */
    fun withNoResult(): CombinatorBuilder<Unit>

    fun compile(compiler: Compiler<IN>): CompiledParser<IN, OUT>
}