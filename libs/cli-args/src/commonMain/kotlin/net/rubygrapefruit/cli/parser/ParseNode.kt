package net.rubygrapefruit.cli.parser

/**
 * Represents some expectation of the next input argument
 */
internal sealed interface ParseNode {
    fun edges(dest: MutableList<ParseEdge>)
}

internal sealed interface ParseEdge {
    sealed interface Matcher : ParseEdge {
        fun next(args: InputArgs): Transition
    }

    sealed interface NonPositional : Matcher

    sealed interface Positional : Matcher

    sealed interface EndOfInput : ParseEdge {
        fun value(): Any?
    }

    sealed interface Transition {
        object NotMatched : Transition
        class Matched(val count: Int, val next: ParseNode) : Transition
        class Failed(val count: Int, val problem: ProblemAtIndex) : Transition
    }
}

internal class Finished(val value: Any?) : ParseNode, ParseEdge.EndOfInput {
    override fun value(): Any? {
        return value
    }

    override fun edges(dest: MutableList<ParseEdge>) {
        dest.add(this)
    }
}
