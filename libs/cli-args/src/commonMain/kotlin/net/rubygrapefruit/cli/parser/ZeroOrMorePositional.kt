package net.rubygrapefruit.cli.parser

internal class ZeroOrMorePositional<OUT>(
    private val name: String,
    private val type: StringTypeInternal<OUT>,
    private val summary: String?
) : AbstractParser<List<OUT>>(), Positional<List<OUT>> {
    override fun edges(consumer: (ParseEdge) -> Unit) {
        ZeroOrMorePositionalEdge(emptyList(), DefaultPositional.PositionalEdge(name, type)).edges(consumer)
    }

    override fun usage(): UsageInternal {
        return ZeroOrMorePositionalUsage(name, summary)
    }
}