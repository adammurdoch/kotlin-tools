package net.rubygrapefruit.cli.parser

internal class StringOptionEdge(val flags: List<String>, val names: List<String>) : ParseEdge.NonPositional {
    override fun maybeClaimUnrecognized(args: InputArgs): Problem? {
        val first = args.first()
        names.forEachIndexed { index, name ->
            if (first.equals("-$name", true) || first.equals("--$name", true)) {
                return UnexpectedOption(first, flags[index])
            }
        }
        return null
    }

    override fun next(args: InputArgs): ParseEdge.Transition {
        val first = args.first()
        return if (flags.contains(first)) {
            if (args.hasSecond() && !args.secondIsOption()) {
                val value = args.second()
                ParseEdge.Transition.Matched(2, OptionValue(value, first, this))
            } else {
                ParseEdge.Transition.Failed(1, ProblemAtIndex(args.index + 1, MissingOptionValue(first)))
            }
        } else {
            ParseEdge.Transition.NotMatched
        }
    }
}

private class OptionValue(val value: String, val matchedFlag: String, val source: StringOptionEdge) : ParseEdge.EndOfInput, ParseNode {
    override fun edges(dest: MutableList<ParseEdge>) {
        dest.add(this)
    }

    override fun maybeClaimUnrecognized(args: InputArgs): Problem? {
        val first = args.first()
        return if (source.flags.contains(first)) {
            if (first == matchedFlag) {
                RepeatedOption(first, null)
            } else {
                RepeatedOption(first, matchedFlag)
            }
        } else {
            source.maybeClaimUnrecognized(args)
        }
    }

    override fun value(): Any {
        return value
    }
}
