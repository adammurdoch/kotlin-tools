package net.rubygrapefruit.parse

internal interface CombinatorBuilder<OUT> {
    fun <IN : Input<*>> compile(converter: Compiler<IN>): CompiledParser<IN, OUT>

    interface Compiler<IN> {
        fun <OUT> compile(parser: Parser<*, OUT>): CompiledParser<IN, OUT>
    }
}
