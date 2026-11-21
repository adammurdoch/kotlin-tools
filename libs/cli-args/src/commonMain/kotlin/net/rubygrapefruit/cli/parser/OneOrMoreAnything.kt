package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal object OneOrMoreAnything : AbstractParser<List<String>>(), Positional<List<String>> {
    override fun edges(consumer: (ParseEdge) -> Unit) {
        OneOrMorePositionalEdge(AnythingParser.AnythingEdge).edges(consumer)
    }

    override fun usage(): UsageInternal {
        return Usage
    }

    private data object Usage : UsageInternal {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("<argument>...")
        }
    }
}