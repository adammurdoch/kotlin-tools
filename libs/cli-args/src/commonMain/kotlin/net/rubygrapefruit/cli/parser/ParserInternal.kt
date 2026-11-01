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
        constructor(index: Int, message: String) : this(listOf(Parser.Problem(index, message)))

        override fun get(): Nothing {
            throw ArgParseException("Argument parsing failed.")
        }
    }
}

internal interface ParseState<out T> {
    /**
     * Attempt to parse the first argument(s) of the provided arguments.
     *
     * May be called zero or more times.
     */
    fun maybeParse(args: InputArgs): Result

    /**
     * Produces the result of this parser.
     */
    fun finish(): FinalResult<T>

    sealed interface Result

    sealed interface FinalResult<out T>

    object NotRecognized : Result

    class Failure(val count: Int, val index: Int, val message: String) : Result

    class Continue(val count: Int) : Result

    class FinalFailure(val messages: List<String>) : FinalResult<Nothing>

    class Success<T>(val result: T) : FinalResult<T>
}