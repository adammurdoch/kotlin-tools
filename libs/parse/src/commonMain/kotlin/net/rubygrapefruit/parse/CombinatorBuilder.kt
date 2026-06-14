package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.stream.Input

/**
 * A parser that can be compiled into a [CompiledParser].
 */
internal interface CombinatorBuilder<out OUT> {
    /**
     * Compiles this parser for the given type of input.
     */
    fun <IN : Input<*>> compile(compiler: Compiler<IN>): CompiledParser<IN, OUT>

    interface Compiler<IN> {
        fun <OUT> compile(parser: Parser<*, OUT>): CompiledParser<IN, OUT>

        fun <OUT> compileRecursive(outer: Parser<*, OUT>, compiledOuter: CompiledParser<IN, OUT>, parser: Parser<*, OUT>): CompiledParser<IN, OUT>

        fun compileWithNoResult(parser: Parser<*, *>): CompiledParser<IN, Unit>

        fun <OUT> maybeAsSingleInputParser(parser: Parser<*, OUT>): LookaheadOneParser<IN, OUT>?
    }
}
