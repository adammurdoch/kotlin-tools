package net.rubygrapefruit.cli.parser

internal abstract class OptionEdge(val flags: List<String>, val names: List<String>) : ParseEdge.NonPositional {
    override fun maybeClaimUnrecognized(args: InputArgs): Problem? {
        val first = args.first()
        names.forEachIndexed { index, name ->
            if (first.equals("-$name", true) || first.equals("--$name", true)) {
                return UnexpectedOption(first, flags[index])
            }
        }
        return null
    }
}

internal class FlagEdge(flags: List<String>, names: List<String>, val value: Any) : OptionEdge(flags, names) {
    override fun next(args: InputArgs): ParseEdge.Transition {
        val first = args.first()
        return if (flags.contains(first)) {
            ParseEdge.Transition.Matched(1, FixedValue(value))
        } else {
            ParseEdge.Transition.NotMatched
        }
    }
}

internal class StringOptionEdge(flags: List<String>, names: List<String>) : OptionEdge(flags, names) {
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

private class OptionValue(val value: Any, val matchedFlag: String, val source: OptionEdge) : ParseEdge.EndOfInput, ParseNode {
    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(this)
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

internal class RequiredOption(val flags: List<String>) : ParseEdge.NotEndOfInput {
    override fun abort(): Problem {
        val longest = flags.maxBy { it.length }
        return MissingOption(longest)
    }
}
