package net.rubygrapefruit.cli.parser

/**
 * Represents some expectation of the next input argument
 */
internal sealed interface ParseNode {
    /**
     * Constraint: must include at least one [ParseEdge.EndOfInput] or [ParseEdge.Matcher] with non-empty [ParseEdge.Matcher.expectingArgument] to specify how to stop parsing at the (possibly unexpected) end of the inputs
     * Cannot include both [ParseEdge.EndOfInput] and [ParseEdge.Matcher] with non-empty [ParseEdge.Matcher.expectingArgument].
     * Can include both [ParseEdge.EndOfInput] and [ParseEdge.Failed], in which case [ParseEdge.EndOfInput] wins
     */
    fun edges(consumer: (ParseEdge) -> Unit)
}

internal sealed interface ParseEdge {
    fun maybeClaimUnrecognized(args: InputArgs): Claimed? {
        return null
    }

    class Claimed(val count: Int, val problem: Problem)

    sealed interface Matcher : ParseEdge {
        val positional: Boolean

        /**
         * Used for a mismatched value, e.g "found x, expected y"
         */
        val expectingValue: List<String>

        /**
         * Used for a missing value, e.g. "expected y"
         *
         * This is also used as a key to identify the argument
         */
        val expectingArgument: String?

        fun next(args: InputArgs): Transition
    }

    sealed interface Terminating : ParseEdge

    /**
     * Expects end of input, then succeeds with the given value
     */
    sealed interface EndOfInput : Terminating {
        fun value(): Any?
    }

    /**
     * Cannot proceed.
     */
    sealed interface Failed : Terminating {
        val positional: Boolean

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
