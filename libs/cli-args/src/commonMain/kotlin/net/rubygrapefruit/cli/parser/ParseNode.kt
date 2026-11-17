package net.rubygrapefruit.cli.parser

/**
 * Represents some expectation of the next input argument
 */
internal sealed interface ParseNode {
    fun edges(dest: MutableList<ParseEdge>)
}

internal sealed interface ParseEdge {
    sealed interface NonPositional : ParseEdge {
        fun next(args: InputArgs): Transition
    }

    sealed interface EndOfInput : ParseEdge {
        val value: Any?
    }

    sealed interface Transition {
        object NotMatched : Transition
        class Matched(val count: Int, val next: ParseNode) : Transition
    }
}

internal class Finished(override val value: Any?) : ParseNode, ParseEdge.EndOfInput {
    override fun edges(dest: MutableList<ParseEdge>) {
        dest.add(this)
    }
}
