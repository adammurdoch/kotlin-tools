package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultStringOption(private val names: List<String>) : AbstractParser<String?>(), StringOption {
    private val flags = names.map { name -> if (name.length == 1) "-$name" else "--$name" }.sortedWith { a, b ->
        val diff = a.length.compareTo(b.length)
        if (diff != 0) {
            diff
        } else {
            a.compareTo(b)
        }
    }

    override fun boolean(): BooleanOption {
        return DefaultBooleanOption(flags.first(), names.first())
    }

    override fun start(): ParseState<String?> {
        return State(flags, names)
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal {
        override fun writeTo(output: Output) {
            output.write("[")
            flags.forEachIndexed { index, flag ->
                if (index > 0) {
                    output.write(" | ")
                }
                output.write("$flag <value>")
            }
            output.write("]")
        }
    }

    private class State(private val flags: List<String>, private val names: List<String>) : ParseState<String?> {
        val flag = flags.first()
        val name = names.first()

        private var seen = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult<String?> {
            val first = args.first()
            if (seen || !flags.contains(first)) {
                return ParseState.NotRecognized
            }
            seen = true
            return if (!args.hasSecond() || args.secondIsOption()) {
                ParseState.Failure(1, ProblemAtIndex(args.index + 1, MissingOptionValue(flag)))
            } else {
                val value = args.second()
                ParseState.Finished(2, ParseState.FixedResult(value))
            }
        }

        override fun claimUnrecognized(args: InputArgs): ParseState.Failure? {
            val first = args.first()
            return if (first == flag) {
                val count = if (args.hasSecond() && !args.secondIsOption() && !args.secondIsRecognized()) 2 else 1
                ParseState.Failure(count, ProblemAtIndex(args.index, RepeatedOption(flag)))
            } else if (first.equals("-$name", true) || first.equals("--$name", true)) {
                val count = if (args.hasSecond() && !args.secondIsOption() && !args.secondIsRecognized()) 2 else 1
                ParseState.Failure(count, ProblemAtIndex(args.index, UnexpectedOption(first, flag)))
            } else {
                null
            }
        }

        override fun finish(index: Int): ParseState.Result<String?> {
            return ParseState.FixedResult(null)
        }
    }
}