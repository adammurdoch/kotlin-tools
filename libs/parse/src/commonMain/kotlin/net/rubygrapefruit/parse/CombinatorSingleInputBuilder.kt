package net.rubygrapefruit.parse

/**
 * A parser that may be converted to a [SingleInputParser].
 */
internal interface CombinatorSingleInputBuilder {
    fun <IN> maybeAsSingleInputParser(compiler: Compiler<IN>): SingleInputParser<IN>?

    interface Compiler<IN> {
        fun maybeAsSingleInputParser(parser: Parser<*, *>): SingleInputParser<IN>?
    }
}