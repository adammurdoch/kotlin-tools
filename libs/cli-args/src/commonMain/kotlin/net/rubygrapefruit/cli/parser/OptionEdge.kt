package net.rubygrapefruit.cli.parser

internal abstract class OptionEdge(val flags: List<String>, val names: List<String>, override val expecting: String?) : ParseEdge.Matcher {
    override val positional: Boolean
        get() = false

    override fun maybeClaimUnrecognized(args: InputArgs): ParseEdge.Claimed? {
        val first = args.first()
        names.forEachIndexed { index, name ->
            if (first.equals("-$name", true) || first.equals("--$name", true)) {
                return ParseEdge.Claimed(claim(args), BadlyFormedOption(first, flags[index]))
            }
        }
        return null
    }

    abstract fun claim(args: InputArgs): Int
}

internal class FlagEdge(flags: List<String>, names: List<String>, expecting: String?, val value: Any) : OptionEdge(flags, names, expecting) {
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

internal class OptionEdgeWithValue(flags: List<String>, names: List<String>, expecting: String?, val type: StringTypeInternal<*>) : OptionEdge(flags, names, expecting) {
    override fun next(args: InputArgs): ParseEdge.Transition {
        val first = args.first()
        return if (flags.contains(first)) {
            if (args.hasSecond() && !args.secondIsOption()) {
                val result = type.convert(args.second())
                when (result) {
                    is StringTypeInternal.Success<*> -> ParseEdge.Transition.Matched(2, OptionValue(result.value, first, this))
                    is StringTypeInternal.Failure -> ParseEdge.Transition.Matched(2, BadlyFormedOptionValue(first, this, args.index + 1, ValueIsNotAnInt(args.second())))
                }
            } else {
                ParseEdge.Transition.Matched(1, BadlyFormedOptionValue(first, this, args.index + 1, MissingOptionValue(first)))
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

private class BadlyFormedOptionValue(matchedFlag: String, source: OptionEdge, val index: Int, val problem: Problem) : AfterOptionEdge(matchedFlag, source), ParseEdge.Failed {
    override val positional: Boolean
        get() = false

    override fun failure(): ProblemAtIndex {
        return ProblemAtIndex(index, problem)
    }
}

private class OptionValue(val value: Any?, matchedFlag: String, source: OptionEdge) : AfterOptionEdge(matchedFlag, source), ParseEdge.EndOfInput {
    override fun value(): Any? {
        return value
    }
}
