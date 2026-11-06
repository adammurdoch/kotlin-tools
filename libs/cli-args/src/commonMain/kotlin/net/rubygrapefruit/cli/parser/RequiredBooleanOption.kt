package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class RequiredBooleanOption(private val flag: String, private val name: String) : AbstractParser<Unit>() {
    override fun start(): ParseState<Unit> {
        return State(flag, name)
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal {
        override fun writeTo(output: Output) {
            output.write(flag)
        }
    }

    private class State(val flag: String, val name: String) : ParseState<Unit> {
        private var claimed = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult<Unit> {
            val first = args.first()
            return if (first != flag) {
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