package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultStringPositional(private val name: String, private val summary: String?) : AbstractParser<String>(), StringPositional {
    override fun oneOrMore(): Positional<List<String>> {
        return DefaultOneOrMorePositional(name, summary)
    }

    override fun zeroOrMore(): Positional<List<String>> {
        return DefaultZeroOrMorePositional(name, summary)
    }

    override fun start(): ParseState<String> {
        return State(name)
    }

    override fun edges(dest: MutableList<ParseEdge>) {
        dest.add(PositionalEdge)
        dest.add(RequiredPositional(name))
    }

    override fun usage(): UsageInternal {
        return PositionalUsage()
    }

    private inner class PositionalUsage : UsageInternal.PositionalUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("<$name>")
        }

        override fun writePositionalUsageTo(output: Output) {
            output.write("<$name>")
        }

        override fun writePositionalSummaryTo(output: Output) {
            output.writeNullable(summary)
        }
    }

    private object PositionalEdge : ParseEdge.Positional {
        override fun next(args: InputArgs): ParseEdge.Transition {
            return if (args.firstIsOption()) {
                ParseEdge.Transition.NotMatched
            } else {
                ParseEdge.Transition.Matched(1, FixedValue(args.first()))
            }
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
                ParsedArguments.FixedResult(result!!)
            } else {
                ParsedArguments.FixedProblems(listOf(ProblemAtIndex(index, MissingPositional(name))))
            }
        }
    }

}