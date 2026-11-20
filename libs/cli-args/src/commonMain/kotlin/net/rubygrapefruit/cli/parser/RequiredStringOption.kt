package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class RequiredStringOption(
    private val flags: List<String>,
    private val names: List<String>,
    private val summary: String?,
    private val valueName: String
) : AbstractParser<String>(), Option<String> {
    private val expecting = "option " + flags.maxBy { it.length }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(StringOptionEdge(flags, names, expecting))
    }

    private inner class OptionUsage : UsageInternal.OptionUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            writeChoices(quoted, output, flags) { flag ->
                output.write("$flag <$valueName>")
            }
        }

        override fun writeOptionUsageTo(output: Output) {
            writeList(output, flags) { flag ->
                output.write("$flag <$valueName>")
            }
        }

        override fun writeOptionSummaryTo(output: Output) {
            output.writeNullable(summary)
        }
    }
}