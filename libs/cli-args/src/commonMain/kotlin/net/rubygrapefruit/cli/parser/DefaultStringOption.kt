package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultStringOption(unsortedNames: List<String>) : AbstractParser<String?>(), StringOption {
    private val names = unsortedNames.sortedWith { a, b ->
        val diff = a.length.compareTo(b.length)
        if (diff != 0) {
            diff
        } else {
            a.compareTo(b)
        }
    }
    private val flags = names.map { name -> if (name.length == 1) "-$name" else "--$name" }

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
    }

    private class State(flags: List<String>, names: List<String>) : OptionParseState<String?>(flags, names) {
        override fun matched(args: InputArgs): ParseState.Finished<String?> {
            return if (!args.hasSecond() || args.secondIsOption()) {
                ParseState.Finished(1, ProblemAtIndex(args.index + 1, MissingOptionValue(args.first())))
            } else {
                val value = args.second()
                ParseState.Finished(2, ParseState.FixedResult(value))
            }
        }

        override fun consumeBadlyFormed(args: InputArgs): Int {
            return if (args.hasSecond() && !args.secondIsOption() && !args.secondIsRecognized()) 2 else 1
        }

        override fun finish(index: Int): ParseState.Result<String?> {
            return ParseState.FixedResult(null)
        }
    }
}