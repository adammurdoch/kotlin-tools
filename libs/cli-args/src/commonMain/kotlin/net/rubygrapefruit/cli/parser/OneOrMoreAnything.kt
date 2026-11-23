package net.rubygrapefruit.cli.parser

internal class OneOrMoreAnything(private val name: String, private val summary: String?) : AbstractParser<List<String>>(), Positional<List<String>> {
    override fun edges(consumer: (ParseEdge) -> Unit) {
        OneOrMorePositionalEdge(AnythingParser.AnythingEdge(name)).edges(consumer)
    }

    override fun usage(): UsageInternal {
        return OneOrMorePositionalUsage(name, summary)
    }
}