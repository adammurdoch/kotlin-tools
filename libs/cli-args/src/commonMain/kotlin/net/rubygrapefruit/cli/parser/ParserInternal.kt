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
    fun maybeParse(args: InputArgs): PartialResult

    /**
     * Produces the result of this parser.
     */
    fun finish(): Result<T>

    sealed interface PartialResult

    sealed interface Result<out T>

    object NotRecognized : PartialResult

    class Failure(val count: Int, val index: Int, val message: String) : PartialResult

    class Continue(val count: Int) : PartialResult

    class FinalFailure(val messages: List<String>) : Result<Nothing>

    sealed interface Success<out T> : Result<T> {
        val result: T
    }

    class FixedResult<T>(override val result: T) : Success<T>

    class LazyResult<T>(val provider: () -> T) : Success<T> {
        override val result: T
            get() = provider()
    }
}