package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultPositional(private val name: String) : AbstractParser<String>(), Positional<String> {
    override fun start(): ParseState<String> {
        return State(name)
    }

    override fun usage(): UsageInternal {
        return ParameterUsage()
    }

    private inner class ParameterUsage : UsageInternal {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("<$name>")
        }
    }

    private class State(val name: String) : ParseState<String> {
        private var result: String? = null

        override val expectingOption: Boolean
            get() = false

        override val expectingPositional: Boolean
            get() = result == null

        override fun maybeParse(args: InputArgs): ParseState.Result {
            return if (args.firstIsOption()) {
                ParseState.NotRecognized
            } else {
                result = args.first()
                ParseState.Recognized(1)
            }
        }

        override fun finish(index: Int): ParsedArguments<String> {
            return if (result != null) {
                Success(result!!)
            } else {
                Failure(listOf(ProblemAtIndex(index, MissingParameter(name))))
            }
        }
    }

    private class Success(val value: String) : ParsedArguments.Success<String> {
        override fun value(): String {
            return value
        }
    }

    private class Failure(val problems: List<ProblemAtIndex>) : ParsedArguments.Failure {
        override fun problems(): List<ProblemAtIndex> {
            return problems
        }
    }
}