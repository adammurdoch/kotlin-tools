package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultParameter(private val name: String) : AbstractParser<String>(), Parameter<String> {
    override fun start(): ParseState<String> {
        return State(name)
    }

    override fun usage(): UsageInternal {
        return ParameterUsage()
    }

    private inner class ParameterUsage : UsageInternal {
        override fun writeTo(output: Output) {
            output.write("<$name>")
        }
    }

    private class State(val name: String) : ParseState.Positional<String> {
        override fun maybeParse(args: InputArgs): ParseState.PartialResult<String> {
            return if (args.firstIsOption()) {
                ParseState.NotRecognized
            } else {
                val value = args.first()
                ParseState.Finished(1, ParseState.FixedResult(value))
            }
        }

        override fun finish(index: Int): ParseState.Result<String> {
            return ParseState.FinalFailure(listOf(ProblemAtIndex(index, MissingParameter(name))))
        }
    }
}