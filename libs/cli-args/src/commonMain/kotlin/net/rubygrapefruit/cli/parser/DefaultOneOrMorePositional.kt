package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultOneOrMorePositional(private val name: String, private val summary: String?) : AbstractParser<List<String>>(), Positional<List<String>> {
    override fun start(): ParseState<List<String>> {
        return State(name)
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(OneOrMorePositionalEdge())
        consumer(RequiredPositional(name))
    }

    override fun usage(): UsageInternal {
        return PositionalUsage()
    }

    private inner class PositionalUsage : UsageInternal.PositionalUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("<$name>...")
        }

        override fun writePositionalUsageTo(output: Output) {
            output.write("<$name>")
        }

        override fun writePositionalSummaryTo(output: Output) {
            output.writeNullable(summary)
        }
    }

    private class OneOrMorePositionalEdge : ParseEdge.Positional {
        override fun next(args: InputArgs): ParseEdge.Transition {
            return if (args.firstIsOption()) {
                ParseEdge.Transition.NotMatched
            } else {
                ParseEdge.Transition.Matched(1, ZeroOrMorePositionalEdge(listOf(args.first())))
            }
        }
    }

    private class State(private val name: String) : ParseState<List<String>> {
        private val result = mutableListOf<String>()

        override val expectingOption: Boolean
            get() = false

        override val expectingPositional: Boolean
            get() = true

        override fun maybeParse(args: InputArgs): ParseState.Result {
            return if (args.firstIsOption()) {
                ParseState.NotRecognized
            } else {
                result.add(args.first())
                ParseState.Recognized(1)
            }
        }

        override fun finish(index: Int): ParsedArguments<List<String>> {
            return if (result.isNotEmpty()) {
                ParsedArguments.FixedResult(result)
            } else {
                ParsedArguments.FixedProblems(listOf(ProblemAtIndex(index, MissingPositional(name))))
            }
        }
    }
}