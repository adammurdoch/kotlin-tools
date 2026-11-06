package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class OptionWithValue(name: String) : AbstractParser<String?>(), StringOption {
    private val flag = if (name.length == 1) "-$name" else "--$name"
    private val incorrectPrefix = if (name.length == 1) "--$name" else "-$name"

    override fun boolean(): Option<Boolean> {
        return BooleanOption(flag, incorrectPrefix)
    }

    override fun start(): ParseState<String?> {
        return State(flag, incorrectPrefix)
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal {
        override fun writeTo(output: Output) {
            output.write("[$flag <value>]")
        }
    }

    private class State(private val flag: String, private val incorrectPrefix: String) : ParseState<String?> {
        private var value: String? = null
        private var seen = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
            val first = args.first()
            if (seen || first != flag) {
                return ParseState.NotRecognized
            }
            seen = true
            return if (!args.hasSecond() || args.secondIsOption()) {
                ParseState.Failure(1, 1, MissingOptionValue(flag))
            } else {
                value = args.second()
                ParseState.Matched(2, true)
            }
        }

        override fun claimUnrecognized(args: InputArgs): ParseState.Failure? {
            val first = args.first()
            return if (first == incorrectPrefix) {
                val count = if (args.hasSecond() && !args.secondIsOption() && !args.secondIsRecognized()) 2 else 1
                ParseState.Failure(count, 0, UnexpectedOption(first, flag))
            } else if (first == flag) {
                val count = if (args.hasSecond() && !args.secondIsOption() && !args.secondIsRecognized()) 2 else 1
                ParseState.Failure(count, 0, RepeatedOption(flag))
            } else {
                null
            }
        }

        override fun finish(): ParseState.Result<String?> {
            return ParseState.FixedResult(value)
        }
    }
}