package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class RequiredStringOption(private val flags: List<String>, private val names: List<String>) : AbstractParser<String>(), Option<String> {
    override fun start(): ParseState<String> {
        return State(flags, names)
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal.OptionUsage {
        override fun writeTo(quoted: Boolean, output: Output) {
            writeChoices(quoted, output, flags) { flag ->
                output.write("$flag <value>")
            }
        }

        override fun writeOptionUsageTo(output: Output) {
            writeList(output, flags) { flag ->
                output.write("$flag <value>")
            }
        }
    }

    private class State(flags: List<String>, names: List<String>) : OptionParseState<String>(flags, names) {
        override fun matched(args: InputArgs): ParseState.Finished<String> {
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

        override fun finish(index: Int): ParseState.Result<String> {
            val longest = flags.maxBy { it.length }
            return ParseState.FinalFailure(listOf(ProblemAtIndex(index, MissingOption(longest))))
        }
    }
}