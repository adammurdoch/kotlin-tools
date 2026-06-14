package net.rubygrapefruit.parse

/**
 * A parser that may be converted to a [InputPredicate].
 */
internal interface CombinatorSingleInputBuilder {
    fun <IN> maybeAsSingleInputParser(compiler: Compiler<IN>): InputPredicate<IN>?

    interface Compiler<IN> {
        fun maybeAsSingleInputParser(parser: Parser<*, *>): InputPredicate<IN>?
    }
}