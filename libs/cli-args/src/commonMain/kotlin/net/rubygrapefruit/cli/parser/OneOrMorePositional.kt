package net.rubygrapefruit.cli.parser

internal class OneOrMorePositional<OUT>(
    private val name: String,
    private val type: StringTypeInternal<OUT>,
    private val summary: String?
) : AbstractParser<List<OUT>>(), Positional<List<OUT>> {

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(OneOrMorePositionalEdge(DefaultPositional.PositionalEdge(name, type)))
    }

    override fun usage(): UsageInternal {
        return OneOrMorePositionalUsage(name, summary)
    }

}