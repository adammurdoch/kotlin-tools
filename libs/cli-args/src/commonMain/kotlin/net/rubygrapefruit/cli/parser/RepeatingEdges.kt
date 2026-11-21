package net.rubygrapefruit.cli.parser

internal interface SingleArgumentMatcher {
    val expecting: String
    fun matches(args: InputArgs): Boolean
}

internal class ZeroOrMorePositionalEdge(val value: List<String>, val matcher: SingleArgumentMatcher) : ParseEdge.Matcher, ParseNode {
    override val positional: Boolean
        get() = true

    override val expecting: String?
        get() = null

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(this)
        consumer(FixedValue(value))
    }

    override fun next(args: InputArgs): ParseEdge.Transition {
        return if (matcher.matches(args)) {
            ParseEdge.Transition.Matched(1, ZeroOrMorePositionalEdge(value + args.first(), matcher))
        } else {
            ParseEdge.Transition.NotMatched
        }
    }
}

internal class OneOrMorePositionalEdge(val matcher: SingleArgumentMatcher) : ParseEdge.Matcher, ParseNode {
    override val positional: Boolean
        get() = true

    override val expecting: String
        get() = matcher.expecting

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(this)
    }

    override fun next(args: InputArgs): ParseEdge.Transition {
        return if (matcher.matches(args)) {
            ParseEdge.Transition.Matched(1, ZeroOrMorePositionalEdge(listOf(args.first()), matcher))
        } else {
            ParseEdge.Transition.NotMatched
        }
    }
}

