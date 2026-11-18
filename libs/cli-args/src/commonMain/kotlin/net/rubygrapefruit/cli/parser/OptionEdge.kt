package net.rubygrapefruit.cli.parser

internal abstract class OptionEdge(val flags: List<String>, val names: List<String>) : ParseEdge.NonPositional {
    override fun maybeClaimUnrecognized(args: InputArgs): ParseEdge.Claimed? {
        val first = args.first()
        names.forEachIndexed { index, name ->
            if (first.equals("-$name", true) || first.equals("--$name", true)) {
                return ParseEdge.Claimed(claim(args), UnexpectedOption(first, flags[index]))
            }
        }
        return null
    }

    abstract fun claim(args: InputArgs): Int
}

internal class FlagEdge(flags: List<String>, names: List<String>, val value: Any) : OptionEdge(flags, names) {
    override fun next(args: InputArgs): ParseEdge.Transition {
        val first = args.first()
        return if (flags.contains(first)) {
            ParseEdge.Transition.Matched(1, OptionValue(value, first, this))
        } else {
            ParseEdge.Transition.NotMatched
        }
    }

    override fun claim(args: InputArgs): Int {
        return 1
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
                ParseEdge.Transition.Matched(1, MissingOptionValueEdge(args.index + 1, first, this))
            }
        } else {
            ParseEdge.Transition.NotMatched
        }
    }

    override fun claim(args: InputArgs): Int {
        return if (args.hasSecond() && !args.secondIsOption()) 2 else 1
    }
}

private abstract class AfterOptionEdge(val matchedFlag: String, val source: OptionEdge) : ParseEdge, ParseNode {
    override fun maybeClaimUnrecognized(args: InputArgs): ParseEdge.Claimed? {
        val first = args.first()
        return if (source.flags.contains(first)) {
            if (first == matchedFlag) {
                ParseEdge.Claimed(source.claim(args), RepeatedOption(first, null))
            } else {
                ParseEdge.Claimed(source.claim(args), RepeatedOption(first, matchedFlag))
            }
        } else {
            source.maybeClaimUnrecognized(args)
        }
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(this)
    }
}

private class MissingOptionValueEdge(val index: Int, matchedFlag: String, source: OptionEdge) : AfterOptionEdge(matchedFlag, source), ParseEdge.Failed {
    override fun failure(): ProblemAtIndex {
        return ProblemAtIndex(index, MissingOptionValue(matchedFlag))
    }
}

private class OptionValue(val value: Any, matchedFlag: String, source: OptionEdge) : AfterOptionEdge(matchedFlag, source), ParseEdge.EndOfInput {
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
