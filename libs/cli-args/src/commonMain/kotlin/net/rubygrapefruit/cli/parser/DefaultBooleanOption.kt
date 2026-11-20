package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultBooleanOption(private val flags: List<String>, private val names: List<String>, private val summary: String?) : AbstractParser<Boolean>(), BooleanOption {
    override fun required(): Option<Unit> {
        return RequiredBooleanOption(flags, names, summary)
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(FlagEdge(flags, names, null, true))
        consumer(FixedValue(false))
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal.OptionUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("[")
            writeChoices(false, output, flags) { flag ->
                output.write(flag)
            }
            output.write("]")
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