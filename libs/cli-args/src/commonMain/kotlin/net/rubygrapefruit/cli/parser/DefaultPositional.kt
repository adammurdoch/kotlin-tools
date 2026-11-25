package net.rubygrapefruit.cli.parser

internal class DefaultPositional<OUT>(
    private val name: String,
    private val type: StringTypeInternal<OUT>,
    private val summary: String?
) : AbstractParser<OUT>(), ScalarPositional<OUT> {
    override fun oneOrMore(): Positional<List<OUT>> {
        return OneOrMorePositional(name, type, summary)
    }

    override fun zeroOrMore(): Positional<List<OUT>> {
        return ZeroOrMorePositional(name, type, summary)
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(PositionalEdge(name, type))
    }

    override fun usage(): UsageInternal {
        return PositionalUsage(name, summary)
    }

    class PositionalEdge(val name: String, val type: StringTypeInternal<*>) : ParseEdge.Matcher, SingleArgumentMatcher {
        override val positional: Boolean
            get() = true

        override val expecting: String
            get() = "parameter <$name>"

        override fun maybeClaimUnrecognized(args: InputArgs): ParseEdge.Claimed? {
            return if (args.firstIsOption()) {
                null
            } else {
                val expecting = type.expecting
                if (expecting.isNotEmpty()) {
                    ParseEdge.Claimed(1, UnexpectedArgument(args.first(), expecting))
                } else {
                    null
                }
            }
        }

        override fun next(args: InputArgs): ParseEdge.Transition {
            return match(args) { FixedValue(it) }
        }

        override fun match(args: InputArgs, next: (Any?) -> ParseNode): ParseEdge.Transition {
            return if (args.firstIsOption()) {
                ParseEdge.Transition.NotMatched
            } else {
                val result = type.convert(args.first())
                when (result) {
                    is StringTypeInternal.Success -> ParseEdge.Transition.Matched(1, next(result.value))
                    is StringTypeInternal.Failure -> ParseEdge.Transition.NotMatched
                }
            }
        }
    }
}