package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultLiteral(private val text: String) : AbstractParser<Unit>(), Parser<Unit> {
    override fun start(): ParseState<Unit> {
        return State(text)
    }

    override fun usage(): UsageInternal {
        return LiteralUsage()
    }

    private inner class LiteralUsage : UsageInternal {
        override fun writeTo(output: Output) {
            output.write(text)
        }
    }

    private class State(val text: String) : ParseState.Positional<Unit> {
        private var claimed = false
        private var matched = false

        override fun maybeParse(args: InputArgs): ParseState.PartialResult<Unit> {
            return if (args.firstIsOption() || args.first() != text) {
                ParseState.NotRecognized
            } else {
                matched = true
                ParseState.Finished(1, ParseState.FixedResult(Unit))
            }
        }

        override fun claimUnrecognized(args: InputArgs): ParseState.Failure? {
            return if (!matched && !claimed && !args.firstIsOption()) {
                claimed = true
                ParseState.Failure(1, ProblemAtIndex(args.index, UnexpectedArgument(args.first(), text)))
            } else {
                null
            }
        }

        override fun finish(index: Int): ParseState.Result<Unit> {
            return if (claimed) {
                ParseState.FinalFailure(emptyList())
            } else {
                ParseState.FinalFailure(listOf(ProblemAtIndex(index, MissingLiteral(text))))
            }
        }
    }
}
