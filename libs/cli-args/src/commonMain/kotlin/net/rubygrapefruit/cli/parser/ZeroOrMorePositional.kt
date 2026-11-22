package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class ZeroOrMorePositional<OUT>(
    private val name: String,
    private val type: StringTypeInternal<OUT>,
    private val summary: String?
) : AbstractParser<List<OUT>>(), Positional<List<OUT>> {
    override fun edges(consumer: (ParseEdge) -> Unit) {
        ZeroOrMorePositionalEdge(emptyList(), DefaultPositional.PositionalEdge(name, type)).edges(consumer)
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