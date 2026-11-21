package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class ZeroOrMorePositional(private val name: String, private val summary: String?) : AbstractParser<List<String>>(), Positional<List<String>> {
    private val predicate = { args: InputArgs -> !args.firstIsOption() }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        ZeroOrMorePositionalEdge(emptyList(), predicate).edges(consumer)
    }

    override fun usage(): UsageInternal {
        return PositionalUsage()
    }

    private inner class PositionalUsage : UsageInternal.PositionalUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("[<$name>...]")
        }

        override fun writePositionalUsageTo(output: Output) {
            output.write("<$name>")
        }

        override fun writePositionalSummaryTo(output: Output) {
            output.writeNullable(summary)
        }
    }
}