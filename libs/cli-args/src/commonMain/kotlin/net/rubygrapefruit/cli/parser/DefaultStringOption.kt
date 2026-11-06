package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultStringOption(private val name: String) : AbstractParser<String?>(), StringOption {
    private val flag = if (name.length == 1) "-$name" else "--$name"

    override fun boolean(): BooleanOption {
        return DefaultBooleanOption(flag, name)
    }

    override fun start(): ParseState<String?> {
        return State(flag, name)
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal {
        override fun writeTo(output: Output) {
            output.write("[$flag <value>]")
        }
    }

    private class State(private val flag: String, private val name: String) : ParseState<String?> {
        private var seen = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult<String?> {
            val first = args.first()
            if (seen || first != flag) {
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