package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal object ZeroOrMoreAnything : AbstractParser<List<String>>(), Positional<List<String>> {
    private val predicate = { _: InputArgs -> true }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        ZeroOrMorePositionalEdge(emptyList(), predicate).edges(consumer)
    }

    override fun usage(): UsageInternal {
        return Usage
    }

    private data object Usage : UsageInternal {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("[<argument>...]")
        }
    }
}