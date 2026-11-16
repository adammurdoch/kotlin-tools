package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultBooleanOption(private val flags: List<String>, private val names: List<String>, private val summary: String?) : AbstractParser<Boolean>(), BooleanOption {
    override fun required(): Option<Unit> {
        return RequiredBooleanOption(flags, names, summary)
    }

    override fun start(): ParseState<Boolean> {
        return State(flags, names)
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

    private class State(flags: List<String>, names: List<String>) : OptionParseState<Boolean>(flags, names) {
        override fun matched(args: InputArgs): Finished<Boolean> {
            return Finished.Success(1, true)
        }

        override fun endOfArguments(index: Int): Finished<Boolean> {
            return Finished.Success(0, false)
        }

        override fun consumeBadlyFormed(args: InputArgs): Int {
            return 1
        }
    }
}