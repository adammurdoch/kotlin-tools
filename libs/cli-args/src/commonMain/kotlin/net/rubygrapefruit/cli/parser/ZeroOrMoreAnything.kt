package net.rubygrapefruit.cli.parser

internal class ZeroOrMoreAnything(private val name: String, private val summary: String?) : AbstractParser<List<String>>(), Positional<List<String>> {
    override fun edges(consumer: (ParseEdge) -> Unit) {
        ZeroOrMorePositionalEdge(emptyList(), AnythingParser.AnythingEdge(name)).edges(consumer)
    }

    override fun usage(): UsageInternal {
        return ZeroOrMorePositionalUsage(name, summary)
    }
}