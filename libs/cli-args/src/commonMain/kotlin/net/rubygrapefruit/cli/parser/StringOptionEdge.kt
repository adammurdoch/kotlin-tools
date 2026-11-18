package net.rubygrapefruit.cli.parser

internal class StringOptionEdge(val flags: List<String>) : ParseEdge.NonPositional {
    override fun next(args: InputArgs): ParseEdge.Transition {
        return if (flags.contains(args.first())) {
            if (args.hasSecond() && !args.secondIsOption()) {
                val value = args.second()
                ParseEdge.Transition.Matched(2, OptionValue(value, flags))
            } else {
                ParseEdge.Transition.Failed(1, ProblemAtIndex(args.index + 1, MissingOptionValue(args.first())))
            }
        } else {
            ParseEdge.Transition.NotMatched
        }
    }
}

private class OptionValue(val value: String, val flags: List<String>) : ParseEdge.EndOfInput, ParseNode {
    override fun edges(dest: MutableList<ParseEdge>) {
        dest.add(this)
    }

    override fun maybeClaimUnrecognized(args: InputArgs): Problem? {
        return if (flags.contains(args.first())) {
            RepeatedOption(args.first())
        } else {
            null
        }
    }

    override fun value(): Any {
        return value
    }
}
