package net.rubygrapefruit.cli.parser

internal interface SingleArgumentMatcher {
    val expecting: String
    fun match(args: InputArgs, next: (Any?) -> ParseNode): ParseEdge.Transition
}

internal class ZeroOrMorePositionalEdge(val value: List<Any?>, val matcher: SingleArgumentMatcher) : ParseEdge.Matcher, ParseNode {
    override val positional: Boolean
        get() = true

    override val expecting: String?
        get() = null

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(this)
        consumer(FixedValue(value))
    }

    override fun next(args: InputArgs): ParseEdge.Transition {
        return matcher.match(args) { ZeroOrMorePositionalEdge(value + it, matcher) }
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
        return matcher.match(args) { ZeroOrMorePositionalEdge(listOf(it), matcher) }
    }
}

