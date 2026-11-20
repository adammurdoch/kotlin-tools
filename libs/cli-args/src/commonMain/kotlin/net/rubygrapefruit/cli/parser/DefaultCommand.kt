package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultCommand(private val text: String) : AbstractParser<Unit>(), Parser<Unit> {

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(CommandEdge(text))
        consumer(RequiredCommand(text))
    }

    override fun usage(): UsageInternal {
        return LiteralUsage()
    }

    private inner class LiteralUsage : UsageInternal {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write(text)
        }
    }

    private class CommandEdge(private val text: String) : ParseEdge.Positional {
        override fun next(args: InputArgs): ParseEdge.Transition {
            return if (args.first() == text) {
                ParseEdge.Transition.Matched(1, FixedValue(Unit))
            } else {
                ParseEdge.Transition.NotMatched
            }
        }
    }

    private class RequiredCommand(private val text: String) : ParseEdge.NotEndOfInput {
        override val expecting: String
            get() = "'$text'"
    }
}
