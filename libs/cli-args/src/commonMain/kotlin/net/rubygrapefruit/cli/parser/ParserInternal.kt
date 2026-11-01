package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.ArgParseException

internal interface ParserInternal<out T> {
    fun start(): ParseState<T>

    class Success<out T>(override val result: T) : Parser.Success<T> {
        override fun get(): T {
            return result
        }
    }

    class Failure(override val problems: List<Parser.Problem>) : Parser.Failure {
        override fun get(): Nothing {
            throw ArgParseException("Argument parsing failed.")
        }
    }
}

internal interface ParseState<out T> {
    /**
     * Attempt to parse the first arguments of the provided arguments.
     *
     * May be called zero or more times.
     */
    fun maybeParse(args: InputArgs): Result

    /**
     * Produces the result of this parser.
     */
    fun finish(): Parser.Result<T>

    sealed interface Result

    object NotRecognized : Result

    class Failure(val count: Int, val index: Int, val message: String) : Result

    class Success(val count: Int) : Result
}