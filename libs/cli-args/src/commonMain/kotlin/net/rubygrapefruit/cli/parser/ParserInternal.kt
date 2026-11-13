package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.BufferingOutput
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
            val output = BufferingOutput()
            output.write("Argument parsing failed")
            for (problem in problems) {
                output.maybeNewLine()
                output.write("- ")
                problem.problem.writeTo(output)
            }
            throw ArgumentParseException(output.text)
        }

        override fun writeTo(output: Output) {
            for (problem in problems) {
                problem.problem.writeTo(output)
                output.maybeNewLine()
            }

            val usage = parser.usage()
            output.writeLine()
            usage.writeSummaryTo(output)
        }
    }
}

internal interface ParseState<out T> {
    val expectingOption: Boolean

    val expectingPositional: Boolean

    /**
     * Attempts to parse the first argument(s) of the provided arguments.
     *
     * May be called zero or more times.
     */
    fun maybeParse(args: InputArgs): Result

    /**
     * Produces the result of this parser at the end of the inputs.
     */
    fun finish(index: Int): ParsedArguments<T>

    sealed interface Result

    /**
     * The parser did not recognize any arguments.
     * The parser's [maybeParse] function may be called again after skipping some arguments.
     */
    object NotRecognized : Result

    /**
     * The parser has recognized one or more arguments without any problems.
     * The parser's [maybeParse] function may be called again after skipping at least recognized arguments (and possibly more).
     */
    class Recognized(val count: Int) : Result
}