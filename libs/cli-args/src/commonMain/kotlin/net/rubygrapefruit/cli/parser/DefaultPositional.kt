package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

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
        return PositionalUsage()
    }

    private inner class PositionalUsage : UsageInternal.PositionalUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("<$name>")
        }

        override fun writePositionalUsageTo(output: Output) {
            output.write("<$name>")
        }

        override fun writePositionalSummaryTo(output: Output) {
            output.writeNullable(summary)
        }
    }

    class PositionalEdge(val name: String, val type: StringTypeInternal<*>) : ParseEdge.Matcher, SingleArgumentMatcher {
        override val positional: Boolean
            get() = true

        override val expecting: String
            get() = "parameter <$name>"

        override fun matches(args: InputArgs): Boolean {
            return !args.firstIsOption()
        }

        override fun next(args: InputArgs): ParseEdge.Transition {
            return if (args.firstIsOption()) {
                ParseEdge.Transition.NotMatched
            } else {
                val result = type.convert(args.first())
                val node = when (result) {
                    is StringTypeInternal.Success -> FixedValue(result.value)
                    is StringTypeInternal.Failure -> BadlyFormedValue(args.index, result.problem)
                }
                ParseEdge.Transition.Matched(1, node)
            }
        }
    }

    private class BadlyFormedValue(val index: Int, val problem: Problem) : ParseNode, ParseEdge.Failed {
        override val positional: Boolean
            get() = true

        override fun edges(consumer: (ParseEdge) -> Unit) {
            consumer(this)
        }

        override fun failure(): ProblemAtIndex {
            return ProblemAtIndex(index, problem)
        }
    }
}