package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.BufferingOutput
import net.rubygrapefruit.cli.terminal.Output

internal interface ParserInternal<out T> : ParseNode {
    fun usage(): UsageInternal

    fun parseUsingEdges(args: List<String>): Parser.Result<T>

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

            val choices = usage.choices()
            if (choices.size > 1) {
                output.writeLine()
                output.heading("USAGE:")
                output.writeLine()
                for (choice in choices) {
                    output.write("  ")
                    choice.writeTo(false, output)
                    output.writeLine()
                }
            } else {
                output.writeLine()
                output.heading("USAGE:")
                output.write(" ")
                usage.writeTo(false, output)
                output.writeLine()
            }
            output.writeLine()
        }
    }
}