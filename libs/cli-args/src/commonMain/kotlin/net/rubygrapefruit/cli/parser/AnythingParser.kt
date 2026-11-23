package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class AnythingParser(private val name: String, private val summary: String?) : AbstractParser<String>(), ScalarPositional<String> {
    override fun oneOrMore(): Positional<List<String>> {
        return OneOrMoreAnything(name)
    }

    override fun zeroOrMore(): Positional<List<String>> {
        return ZeroOrMoreAnything(name)
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(AnythingEdge(name))
    }

    override fun usage(): UsageInternal {
        return AnythingUsage()
    }

    private inner class AnythingUsage : UsageInternal.PositionalUsage {
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

    class AnythingEdge(val name: String) : ParseEdge.Matcher, SingleArgumentMatcher {
        override val positional: Boolean
            get() = true

        override val expecting: String
            get() = "parameter <$name>"

        override fun match(args: InputArgs, next: (Any?) -> ParseNode): ParseEdge.Transition {
            return ParseEdge.Transition.Matched(1, next(args.first()))
        }

        override fun next(args: InputArgs): ParseEdge.Transition {
            return ParseEdge.Transition.Matched(1, FixedValue(args.first()))
        }

        override fun maybeClaimUnrecognized(args: InputArgs): ParseEdge.Claimed? {
            return null
        }
    }
}