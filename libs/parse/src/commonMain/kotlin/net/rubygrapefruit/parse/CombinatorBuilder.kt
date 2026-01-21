package net.rubygrapefruit.parse

internal interface CombinatorBuilder<out OUT> {
    fun <IN : Input<*>> compile(compiler: Compiler<IN>): CompiledParser<IN, OUT>

    interface Compiler<IN> {
        fun <OUT> compile(parser: Parser<*, OUT>): CompiledParser<IN, OUT>
    }
}
