package net.rubygrapefruit.parse

/**
 * A parser that may be converted to a [SingleInputParser].
 */
internal interface CombinatorSingleInputBuilder<OUT> {
    fun <IN> maybeAsSingleInputParser(compiler: Compiler<IN>): SingleInputParser<IN, OUT>?

    interface Compiler<IN> {
        fun <OUT> maybeAsSingleInputParser(parser: Parser<*, OUT>): SingleInputParser<IN, OUT>?

        fun compile(predicate: InputPredicate<*>): InputPredicate<IN>
    }
}