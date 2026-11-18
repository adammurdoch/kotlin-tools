package net.rubygrapefruit.cli.parser

internal class ZeroOrMorePositionalEdge(val value: List<String>) : ParseEdge.Positional, ParseNode {
    override fun edges(dest: MutableList<ParseEdge>) {
        dest.add(this)
        dest.add(FixedValue(value))
    }

    override fun next(args: InputArgs): ParseEdge.Transition {
        return if (args.firstIsOption()) {
            ParseEdge.Transition.NotMatched
        } else {
            ParseEdge.Transition.Matched(1, ZeroOrMorePositionalEdge(value + args.first()))
        }
    }
}

internal class RequiredPositional(val name: String) : ParseEdge.NotEndOfInput {
    override fun abort(): Problem {
        return MissingPositional(name)
    }
}
