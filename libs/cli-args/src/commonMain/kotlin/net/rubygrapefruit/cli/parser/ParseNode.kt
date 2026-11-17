package net.rubygrapefruit.cli.parser

/**
 * Represents some expectation of the next input argument
 */
internal sealed interface ParseNode {
    fun edges(dest: MutableList<ParseEdge>)

    object Finished : ParseNode {
        override fun edges(dest: MutableList<ParseEdge>) {
            dest.add(ParseEdge.EndOfInput)
        }
    }
}

internal sealed interface ParseEdge {
    sealed interface NonPositional : ParseEdge {
        fun next(args: InputArgs): ParseNode
    }

    object EndOfInput : ParseEdge
}

