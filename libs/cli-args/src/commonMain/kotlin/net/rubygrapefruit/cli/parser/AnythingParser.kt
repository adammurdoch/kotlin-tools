package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class AnythingParser : AbstractParser<String>(), ScalarPositional<String> {
    override fun oneOrMore(): Positional<List<String>> {
        return OneOrMoreAnything
    }

    override fun zeroOrMore(): Positional<List<String>> {
        return ZeroOrMoreAnything
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(AnythingEdge)
    }

    override fun usage(): UsageInternal {
        return AnythingUsage
    }

    private object AnythingUsage : UsageInternal {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("<argument>")
        }
    }

    object AnythingEdge : ParseEdge.Matcher, SingleArgumentMatcher {
        override val positional: Boolean
            get() = true

        override val expecting: String
            get() = "an argument"

        override fun matches(args: InputArgs): Boolean {
            return true
        }

        override fun next(args: InputArgs): ParseEdge.Transition {
            return ParseEdge.Transition.Matched(1, FixedValue(args.first()))
        }
    }
}