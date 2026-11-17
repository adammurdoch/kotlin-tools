package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class RequiredStringOption(
    private val flags: List<String>,
    private val names: List<String>,
    private val summary: String?,
    private val valueName: String
) : AbstractParser<String>(), Option<String> {
    override fun start(): ParseState<String> {
        return State(flags, names)
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    override fun edges(dest: MutableList<ParseEdge>) {
        dest.add(StringOptionEdge(flags))
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

    private class State(flags: List<String>, names: List<String>) : OptionParseState<String>(flags, names) {
        override fun matched(args: InputArgs): Finished<String> {
            return if (!args.hasSecond() || args.secondIsOption()) {
                Finished.Failure(1, ProblemAtIndex(args.index + 1, MissingOptionValue(args.first())))
            } else {
                val value = args.second()
                Finished.Success(2, value)
            }
        }

        override fun endOfArguments(index: Int): Finished<String> {
            return Finished.MissingOption
        }

        override fun consumeBadlyFormed(args: InputArgs): Int {
            return if (args.hasSecond() && !args.secondIsOption() && !args.secondIsRecognized()) 2 else 1
        }
    }
}