package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class RequiredBooleanOption(private val flags: List<String>, private val names: List<String>, private val summary: String?) : AbstractParser<Unit>(), Option<Unit> {
    override fun start(): ParseState<Unit> {
        return State(flags, names)
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
            if (summary != null) {
                output.write(summary)
            }
        }
    }

    private class State(flags: List<String>, names: List<String>) : OptionParseState<Unit>(flags, names) {
        override fun matched(args: InputArgs): Finished<Unit> {
            return Finished.Success(1, Unit)
        }

        override fun endOfArguments(index: Int): Finished<Unit> {
            return Finished.MissingOption
        }

        override fun consumeBadlyFormed(args: InputArgs): Int {
            return 1
        }
    }
}