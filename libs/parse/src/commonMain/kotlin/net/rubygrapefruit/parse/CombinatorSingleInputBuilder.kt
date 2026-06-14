package net.rubygrapefruit.parse

/**
 * A parser that may be converted to an [InputPredicate].
 */
internal interface CombinatorSingleInputBuilder {
    fun <IN> maybeAsSingleInputParser(compiler: Compiler<IN>): InputPredicate<IN>?

    interface Compiler<IN> {
        fun maybeAsSingleInputParser(parser: Parser<*, *>): InputPredicate<IN>?

        fun compile(predicate: InputPredicate<*>): InputPredicate<IN>
    }
}