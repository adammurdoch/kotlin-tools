package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultStringOption(
    private val names: List<String>,
    private val summary: String?,
    private val valueName: String
) : AbstractParser<String?>(), StringOption {
    private val flags = names.map { name -> if (name.length == 1) "-$name" else "--$name" }

    override fun required(): Option<String> {
        return RequiredStringOption(flags, names, summary, valueName)
    }

    override fun boolean(): BooleanOption {
        return DefaultBooleanOption(flags, names, summary)
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(StringOptionEdge(flags, names))
        consumer(FixedValue(null))
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal.OptionUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("[")
            writeChoices(false, output, flags) { flag ->
                output.write("$flag <$valueName>")
            }
            output.write("]")
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