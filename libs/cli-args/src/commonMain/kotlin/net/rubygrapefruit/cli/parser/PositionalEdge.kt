package net.rubygrapefruit.cli.parser

internal class ZeroOrMorePositionalEdge(val value: List<String>) : ParseEdge.Matcher, ParseNode {
    override val positional: Boolean
        get() = true

    override val expecting: String?
        get() = null

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(this)
        consumer(FixedValue(value))
    }

    override fun next(args: InputArgs): ParseEdge.Transition {
        return if (args.firstIsOption()) {
            ParseEdge.Transition.NotMatched
        } else {
            ParseEdge.Transition.Matched(1, ZeroOrMorePositionalEdge(value + args.first()))
        }
    }
}
