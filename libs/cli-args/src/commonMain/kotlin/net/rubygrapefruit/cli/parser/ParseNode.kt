package net.rubygrapefruit.cli.parser

/**
 * Represents some expectation of the next input argument
 */
internal sealed interface ParseNode {
    /**
     * Constraint: must include at least one [ParseEdge.Terminating] to specify how to stop parsing at the (possibly unexpected) end of the inputs
     * Cannot include both [ParseEdge.EndOfInput] and [ParseEdge.NotEndOfInput].
     */
    fun edges(consumer: (ParseEdge) -> Unit)
}

internal sealed interface ParseEdge {
    fun maybeClaimUnrecognized(args: InputArgs): Claimed? {
        return null
    }

    class Claimed(val count: Int, val problem: Problem)

    sealed interface Matcher : ParseEdge {
        fun next(args: InputArgs): Transition
    }

    /**
     * Expects a non-positional argument
     */
    sealed interface NonPositional : Matcher

    /**
     * Expects a positional argument
     */
    sealed interface Positional : Matcher

    sealed interface Terminating : ParseEdge

    /**
     * Expects end of input, then succeeds with the given value
     */
    sealed interface EndOfInput : Terminating {
        fun value(): Any?
    }

    /**
     * Expects anything other than the end of input.
     */
    sealed interface NotEndOfInput : Terminating {
        val expecting: String
    }

    /**
     * Cannot proceed.
     */
    sealed interface Failed : Terminating {
        fun failure(): ProblemAtIndex
    }

    sealed interface Transition {
        object NotMatched : Transition
        class Matched(val count: Int, val next: ParseNode) : Transition
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
