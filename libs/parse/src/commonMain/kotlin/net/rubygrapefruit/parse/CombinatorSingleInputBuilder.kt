package net.rubygrapefruit.parse

/**
 * A parser that may be converted to a [LookaheadOneParser].
 */
internal interface CombinatorSingleInputBuilder<OUT> {
    fun <IN> maybeAsSingleInputParser(compiler: Compiler<IN>): LookaheadOneParser<IN, OUT>?

    interface Compiler<IN> {
        fun <OUT> maybeAsSingleInputParser(parser: Parser<*, OUT>): LookaheadOneParser<IN, OUT>?

        fun compile(predicate: InputPredicate<*>): InputPredicate<IN>
    }
}