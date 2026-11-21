package net.rubygrapefruit.cli.parser

internal class ZeroOrMorePositionalEdge(val value: List<String>, val predicate: (InputArgs) -> Boolean) : ParseEdge.Matcher, ParseNode {
    override val positional: Boolean
        get() = true

    override val expecting: String?
        get() = null

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(this)
        consumer(FixedValue(value))
    }

    override fun next(args: InputArgs): ParseEdge.Transition {
        return if (predicate(args)) {
            ParseEdge.Transition.Matched(1, ZeroOrMorePositionalEdge(value + args.first(), predicate))
        } else {
            ParseEdge.Transition.NotMatched
        }
    }
}
