package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class RequiredBooleanOption(private val flags: List<String>, private val names: List<String>) : AbstractParser<Unit>() {
    override fun start(): ParseState<Unit> {
        return State(flags, names)
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal {
        override fun writeTo(output: Output) {
            if (flags.size == 1) {
                output.write(flags.first())
            } else {
                output.write("(")
                flags.forEachIndexed { index, flag ->
                    if (index > 0) {
                        output.write(" | ")
                    }
                    output.write(flag)
                }
                output.write(")")
            }
        }
    }

    private class State(val flags: List<String>, val names: List<String>) : ParseState<Unit> {
        private var claimed = false

        val flag = flags.first()
        val name = names.first()

        override fun maybeParse(args: InputArgs): ParseState.PartialResult<Unit> {
            val first = args.first()
            return if (!flags.contains(first)) {
                ParseState.NotRecognized
            } else {
                ParseState.Finished(1, ParseState.FixedResult(Unit))
            }
        }

        override fun claimUnrecognized(args: InputArgs): ParseState.Failure? {
            val first = args.first()
            return if (first == flag) {
                ParseState.Failure(1, ProblemAtIndex(args.index, RepeatedOption(flag)))
            } else if (first.equals("-$name", true) || first.equals("--$name", true)) {
                claimed = true
                ParseState.Failure(1, ProblemAtIndex(args.index, UnexpectedOption(first, flag)))
            } else {
                null
            }
        }

        override fun finish(index: Int): ParseState.Result<Unit> {
            return if (claimed) {
                ParseState.FinalFailure(emptyList())
            } else {
                ParseState.FinalFailure(listOf(ProblemAtIndex(index, MissingOption(flag))))
            }
        }
    }
}