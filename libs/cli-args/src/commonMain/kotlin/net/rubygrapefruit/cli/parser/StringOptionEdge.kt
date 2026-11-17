package net.rubygrapefruit.cli.parser

internal class StringOptionEdge(val flags: List<String>) : ParseEdge.NonPositional {
    override fun next(args: InputArgs): ParseEdge.Transition {
        return if (flags.contains(args.first())) {
            if (args.hasSecond() && !args.secondIsOption()) {
                val value = args.second()
                ParseEdge.Transition.Matched(2, Finished(value))
            } else {
                ParseEdge.Transition.Failed(1, ProblemAtIndex(args.index + 1, MissingOptionValue(args.first())))
            }
        } else {
            ParseEdge.Transition.NotMatched
        }
    }
}
