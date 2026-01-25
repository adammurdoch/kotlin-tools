package net.rubygrapefruit.parse

/**
 * A parser that can be compiled into a [CompiledParser].
 */
internal interface CombinatorBuilder<out OUT> {
    /**
     * Compiles this parser.
     */
    fun <IN : Input<*>> compile(compiler: Compiler<IN>): CompiledParser<IN, OUT>

    interface Compiler<IN> {
        fun <OUT> compile(parser: Parser<*, OUT>): CompiledParser<IN, OUT>

        fun <OUT> compileRecursive(outer: Parser<*, OUT>, compiledOuter: CompiledParser<IN, OUT>, parser: Parser<*, OUT>): CompiledParser<IN, OUT>

        fun compileWithNoResult(parser: Parser<*, *>): CompiledParser<IN, Unit>

        fun maybeAsSingleInputParser(parser: Parser<*, *>): SingleInputParser<IN>?
    }
}
