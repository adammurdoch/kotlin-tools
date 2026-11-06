package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal interface ParserInternal<out T> {
    fun start(): ParseState<T>

    fun usage(): UsageInternal

    class Success<out T>(override val result: T) : Parser.Success<T> {
        override fun get(): T {
            return result
        }
    }

    class Failure(problems: List<ProblemAtIndex>, private val parser: ParserInternal<*>) : Parser.Failure {
        override val problems: List<ProblemAtIndex> = problems.sortedBy { it.index }

        override fun get(): Nothing {
            throw ArgumentParseException()
        }

        override fun writeTo(output: Output) {
            for (problem in problems) {
                problem.problem.writeTo(output)
                output.maybeNewLine()
            }
            output.writeLine()
            output.write("USAGE: ")
            parser.usage().writeTo(output)
            output.writeLine()
        }
    }
}

internal interface ParseState<out T> {
    /**
     * Attempt to parse the first argument(s) of the provided arguments.
     *
     * May be called zero or more times, followed by [finish].
     */
    fun maybeParse(args: InputArgs): PartialResult<T>

    /**
     * Produces the result of this parser.
     */
    fun finish(index: Int): Result<T>

    fun claimUnrecognized(args: InputArgs): Failure? {
        return null
    }

    sealed interface PartialResult<out T>

    /**
     * The parser did not recognize any arguments.
     * The parser's [maybeParse] function may be called again after skipping some arguments.
     */
    object NotRecognized : PartialResult<Nothing>

    /**
     * The parser has recognized one or more arguments but has encountered some problem.
     * The parser's [maybeParse] function may be called again after skipping at least recognized arguments (and possibly more).
     */
    class Failure(val count: Int, val problem: ProblemAtIndex) : PartialResult<Nothing>

    /**
     * The parser has recognized one or more arguments without any problems.
     * The parser's [maybeParse] function may be called again after skipping at least recognized arguments (and possibly more).
     */
    class Matched(val count: Int) : PartialResult<Nothing>

    class Finished<T>(val count: Int, val result: Result<T>) : PartialResult<T>

    sealed interface Result<out T>

    class FinalFailure(val problems: List<ProblemAtIndex>) : Result<Nothing>

    sealed interface Success<out T> : Result<T> {
        val result: T
    }

    class FixedResult<T>(override val result: T) : Success<T>

    class LazyResult<T>(val provider: () -> T) : Success<T> {
        override val result: T
            get() = provider()
    }
}