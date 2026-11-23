package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class ZeroOrMoreAnything(private val name: String) : AbstractParser<List<String>>(), Positional<List<String>> {
    override fun edges(consumer: (ParseEdge) -> Unit) {
        ZeroOrMorePositionalEdge(emptyList(), AnythingParser.AnythingEdge(name)).edges(consumer)
    }

    override fun usage(): UsageInternal {
        return Usage()
    }

    private inner class Usage : UsageInternal.PositionalUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("[<$name>...]")
        }

        override fun writePositionalUsageTo(output: Output) {
            output.write("<$name>")
        }

        override fun writePositionalSummaryTo(output: Output) {
        }
    }
}