package net.rubygrapefruit.cli.parser

internal class AnythingParser(private val name: String, private val summary: String?) : AbstractParser<String>(), ScalarPositional<String> {
    override fun oneOrMore(): Positional<List<String>> {
        return OneOrMoreAnything(name, summary)
    }

    override fun zeroOrMore(): Positional<List<String>> {
        return ZeroOrMoreAnything(name, summary)
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(AnythingEdge(name))
    }

    override fun usage(): UsageInternal {
        return PositionalUsage(name, summary)
    }

    class AnythingEdge(val name: String) : ParseEdge.Matcher, SingleArgumentMatcher {
        override val positional: Boolean
            get() = true

        override val expectingArgument: String
            get() = "parameter <$name>"

        override val expectingValue: List<String>
            get() = emptyList()

        override fun match(args: InputArgs, next: (Any?) -> ParseNode): ParseEdge.Transition {
            return ParseEdge.Transition.Matched(1, next(args.first()))
        }

        override fun next(args: InputArgs): ParseEdge.Transition {
            return ParseEdge.Transition.Matched(1, FixedValue(args.first()))
        }
    }
}