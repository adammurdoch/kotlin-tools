package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultStringOption(unsortedNames: List<String>, private val summary: String?) : AbstractParser<String?>(), StringOption {
    private val names = unsortedNames.sortedWith { a, b ->
        val diff = a.length.compareTo(b.length)
        if (diff != 0) {
            diff
        } else {
            a.compareTo(b)
        }
    }
    private val flags = names.map { name -> if (name.length == 1) "-$name" else "--$name" }

    override fun required(): Option<String> {
        return RequiredStringOption(flags, names)
    }

    override fun boolean(): BooleanOption {
        return DefaultBooleanOption(flags, names)
    }

    override fun start(): ParseState<String?> {
        return State(flags, names)
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal.OptionUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("[")
            writeChoices(false, output, flags) { flag ->
                output.write("$flag <value>")
            }
            output.write("]")
        }

        override fun writeOptionUsageTo(output: Output) {
            writeList(output, flags) { flag ->
                output.write("$flag <value>")
            }
        }

        override fun writeOptionSummaryTo(output: Output) {
            if (summary != null) {
                output.write("  ")
                output.write(summary)
            }
        }
    }

    private class State(flags: List<String>, names: List<String>) : OptionParseState<String?>(flags, names) {
        override fun matched(args: InputArgs): Finished<String?> {
            return if (!args.hasSecond() || args.secondIsOption()) {
                Finished.Failure(1, ProblemAtIndex(args.index + 1, MissingOptionValue(args.first())))
            } else {
                val value = args.second()
                Finished.Success(2, value)
            }
        }

        override fun endOfArguments(index: Int): Finished<String?> {
            return Finished.Success(0, null)
        }

        override fun consumeBadlyFormed(args: InputArgs): Int {
            return if (args.hasSecond() && !args.secondIsOption() && !args.secondIsRecognized()) 2 else 1
        }
    }
}