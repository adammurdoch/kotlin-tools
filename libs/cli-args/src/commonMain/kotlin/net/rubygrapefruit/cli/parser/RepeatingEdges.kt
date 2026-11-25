package net.rubygrapefruit.cli.parser

internal interface SingleArgumentMatcher {
    val expectingArgument: String?

    val expectingValue: List<String>

    fun match(args: InputArgs, next: (Any?) -> ParseNode): ParseEdge.Transition
}

internal class ZeroOrMorePositionalEdge(val value: List<Any?>, val matcher: SingleArgumentMatcher) : ParseEdge.Matcher, ParseNode {
    override val positional: Boolean
        get() = true

    override val expectingValue: List<String>
        get() = matcher.expectingValue

    override val expectingArgument: String?
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

    override val expectingValue: List<String>
        get() = matcher.expectingValue

    override val expectingArgument: String?
        get() = matcher.expectingArgument

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(this)
    }

    override fun next(args: InputArgs): ParseEdge.Transition {
        return matcher.match(args) { ZeroOrMorePositionalEdge(listOf(it), matcher) }
    }
}

