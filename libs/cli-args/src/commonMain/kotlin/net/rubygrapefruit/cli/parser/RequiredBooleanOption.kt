package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class RequiredBooleanOption(private val flags: List<String>, private val names: List<String>, private val summary: String?) : AbstractParser<Unit>(), Option<Unit> {
    private val expecting = "option " + flags.maxBy { it.length }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(FlagEdge(flags, names, expecting, Unit))
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal.OptionUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            writeChoices(quoted, output, flags) { flag ->
                output.write(flag)
            }
        }

        override fun writeOptionUsageTo(output: Output) {
            writeList(output, flags) { flag ->
                output.write(flag)
            }
        }

        override fun writeOptionSummaryTo(output: Output) {
            output.writeNullable(summary)
        }
    }
}