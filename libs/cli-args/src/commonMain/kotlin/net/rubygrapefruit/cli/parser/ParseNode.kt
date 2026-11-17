package net.rubygrapefruit.cli.parser

/**
 * Represents some expectation of the next input argument
 */
internal sealed interface ParseNode {
    fun edges(dest: MutableList<ParseEdge>)
}

internal sealed interface ParseEdge {
    sealed interface NonPositional : ParseEdge {
        fun next(args: InputArgs): ParseNode
    }

    sealed interface EndOfInput : ParseEdge {
        val value: Any?
    }
}

internal class Finished(override val value: Any?) : ParseNode, ParseEdge.EndOfInput {
    override fun edges(dest: MutableList<ParseEdge>) {
        dest.add(this)
    }
}
