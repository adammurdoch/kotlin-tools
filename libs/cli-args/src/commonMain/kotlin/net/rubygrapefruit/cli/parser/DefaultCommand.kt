package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultCommand(private val text: String) : AbstractParser<Unit>(), Parser<Unit> {

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(CommandEdge(text))
    }

    override fun usage(): UsageInternal {
        return LiteralUsage()
    }

    private inner class LiteralUsage : UsageInternal {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write(text)
        }
    }

    private class CommandEdge(private val text: String) : ParseEdge.Matcher {
        override val positional: Boolean
            get() = true

        override val expectingArgument: String
            get() = "'$text'"

        override val expectingValue: List<String>
            get() = listOf(expectingArgument)

        override fun next(args: InputArgs): ParseEdge.Transition {
            return if (args.first() == text) {
                ParseEdge.Transition.Matched(1, FixedValue(Unit))
            } else {
                ParseEdge.Transition.NotMatched
            }
        }
    }
}
