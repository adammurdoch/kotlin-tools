package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultStringPositional(private val name: String, private val summary: String?) : AbstractParser<String>(), StringPositional {
    override fun oneOrMore(): Positional<List<String>> {
        return DefaultOneOrMorePositional(name, summary)
    }

    override fun zeroOrMore(): Positional<List<String>> {
        return DefaultZeroOrMorePositional(name, summary)
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(PositionalEdge)
        consumer(RequiredPositional(name))
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

    private object PositionalEdge : ParseEdge.Matcher {
        override val positional: Boolean
            get() = true

        override fun next(args: InputArgs): ParseEdge.Transition {
            return if (args.firstIsOption()) {
                ParseEdge.Transition.NotMatched
            } else {
                ParseEdge.Transition.Matched(1, FixedValue(args.first()))
            }
        }
    }
}