package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultBooleanOption(private val flags: List<String>, private val names: List<String>) : AbstractParser<Boolean>(), BooleanOption {
    override fun required(): Parser<Unit> {
        return RequiredBooleanOption(flags.first(), names.first())
    }

    override fun start(): ParseState<Boolean> {
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
                output.write(flag)
            }
            output.write("]")
        }
    }

    private class State(val flags: List<String>, val names: List<String>) : ParseState<Boolean> {
        val flag = flags.first()
        val name = names.first()

        override fun maybeParse(args: InputArgs): ParseState.PartialResult<Boolean> {
            val first = args.first()
            return if (!flags.contains(first)) {
                ParseState.NotRecognized
            } else {
                ParseState.Finished(1, ParseState.FixedResult(true))
            }
        }

        override fun claimUnrecognized(args: InputArgs): ParseState.Failure? {
            val first = args.first()
            return if (first == flag) {
                ParseState.Failure(1, ProblemAtIndex(args.index, RepeatedOption(flag)))
            } else if (first.equals("-$name", true) || first.equals("--$name", true)) {
                ParseState.Failure(1, ProblemAtIndex(args.index, UnexpectedOption(first, flag)))
            } else {
                null
            }
        }

        override fun finish(index: Int): ParseState.Result<Boolean> {
            return ParseState.FixedResult(false)
        }
    }
}