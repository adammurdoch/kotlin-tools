package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultOption<OUT>(
    private val names: List<String>,
    private val type: StringTypeInternal<OUT>,
    private val summary: String?,
    private val valueName: String
) : AbstractParser<OUT?>(), ScalarOption<OUT> {
    private val flags = names.map { name -> if (name.length == 1) "-$name" else "--$name" }

    override fun required(): Option<OUT> {
        return DefaultRequiredOption(flags, names, type, summary, valueName)
    }

    override fun boolean(): BooleanOption {
        return DefaultBooleanOption(flags, names, summary)
    }

    override fun edges(consumer: (ParseEdge) -> Unit) {
        consumer(OptionEdgeWithValue(flags, names, null, type))
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