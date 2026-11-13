package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultCommand(private val text: String) : AbstractParser<Unit>(), Parser<Unit> {
    override fun start(): ParseState<Unit> {
        return State(text)
    }

    override fun usage(): UsageInternal {
        return LiteralUsage()
    }

    private inner class LiteralUsage : UsageInternal {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write(text)
        }
    }

    private class State(val text: String) : ParseState<Unit> {
        private var matched = false

        override val finished: Boolean
            get() = matched

        override val expectingOption: Boolean
            get() = false

        override fun maybeParse(args: InputArgs): ParseState.Result {
            return if (args.firstIsOption() || args.first() != text) {
                ParseState.NotRecognized
            } else {
                matched = true
                ParseState.Recognized(1)
            }
        }

        override fun finish(index: Int): ParsedArguments<Unit> {
            return if (matched) {
                Success
            } else {
                Failure(index, text)
            }
        }
    }

    private object Success : ParsedArguments.Success<Unit> {
        override fun value() {
        }
    }

    private class Failure(val index: Int, val text: String) : ParsedArguments.Failure {
        private var suggested = false

        override fun problems(): List<ProblemAtIndex> {
            return if (suggested) {
                emptyList()
            } else {
                listOf(ProblemAtIndex(index, MissingLiteral(text)))
            }
        }

        override fun claimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            return if (!args.firstIsOption()) {
                suggested = true
                ParsedArguments.Unrecognized(1, ProblemAtIndex(args.index, UnexpectedArgument(args.first(), text)))
            } else {
                null
            }
        }
    }
}
