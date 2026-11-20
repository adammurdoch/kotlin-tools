package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class OneOfParser<OUT>(val choices: List<ParserInternal<OUT>>) : AbstractParser<OUT>(), Parser<OUT> {

    override fun edges(consumer: (ParseEdge) -> Unit) {
        for (choice in choices) {
            choice.edges(consumer)
        }
    }

    override fun usage(): UsageInternal {
        return OneOfUsage(choices)
    }

    private class OneOfUsage(choices: List<ParserInternal<*>>) : UsageInternal {
        private val usages = choices.map { it.usage() }

        override fun choices(): List<UsageInternal> {
            return usages
        }

        override fun options(): List<UsageInternal.OptionUsage> {
            return usages.flatMap { it.options() }
        }

        override fun positionals(): List<UsageInternal.PositionalUsage> {
            return usages.flatMap { it.positionals() }
        }

        override fun writeTo(quoted: Boolean, output: Output) {
            writeChoices(quoted, output, usages) { choice ->
                choice.writeTo(true, output)
            }
        }
    }
}