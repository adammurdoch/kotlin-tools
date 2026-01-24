package net.rubygrapefruit.parse

/**
 * A parser that can be compiled into a [CompiledParser].
 */
internal interface CombinatorBuilder<out OUT> {
    /**
     * Creates a copy of this parser that produces no result.
     */
    fun withNoResult(): CombinatorBuilder<Unit>

    /**
     * Compiles this parser.
     */
    fun <IN : Input<*>> compile(compiler: Compiler<IN>): CompiledParser<IN, OUT>

    interface Compiler<IN> {
        fun <OUT> compile(parser: Parser<*, OUT>): CompiledParser<IN, OUT>

        fun compileWithNoResult(parser: Parser<*, *>): CompiledParser<IN, Unit>

        fun maybeAsSingleInputParser(parser: Parser<*, *>): SingleInputParser<IN>?
    }
}
