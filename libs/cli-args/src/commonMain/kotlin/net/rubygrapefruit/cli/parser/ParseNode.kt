package net.rubygrapefruit.cli.parser

/**
 * Represents some expectation of the next input argument
 */
internal sealed interface ParseNode {
    /**
     * Constraint: must include at least one [ParseEdge.NotEndOfInput] or at least one [ParseEdge.EndOfInput].
     * Can include both [ParseEdge.EndOfInput] and [ParseEdge.NotEndOfInput], in which case the required is ignored.
     */
    fun edges(consumer: (ParseEdge) -> Unit)
}

internal sealed interface ParseEdge {
    fun maybeClaimUnrecognized(args: InputArgs): Problem? {
        return null
    }

    sealed interface Matcher : ParseEdge {
        fun next(args: InputArgs): Transition
    }

    sealed interface NonPositional : Matcher

    sealed interface Positional : Matcher

    sealed interface NotEndOfInput : ParseEdge {
        fun abort(): Problem
    }

    sealed interface EndOfInput : ParseEdge {
        fun value(): Any?
    }

    sealed interface Transition {
        object NotMatched : Transition
        class Matched(val count: Int, val next: ParseNode) : Transition
        class Failed(val count: Int, val problem: ProblemAtIndex) : Transition
    }
}

internal class FixedValue(val value: Any?) : ParseNode, ParseEdge.EndOfInput {
    override fun value(): Any? {
        return value
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(this)
    }
}

internal class LazyValue(val supplier: () -> Any?) : ParseNode, ParseEdge.EndOfInput {
    override fun value(): Any? {
        return supplier()
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(this)
    }
}
