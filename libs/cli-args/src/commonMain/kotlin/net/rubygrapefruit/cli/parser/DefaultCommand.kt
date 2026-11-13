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

        override fun maybeParse(args: InputArgs): ParseState.PartialResult {
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
                Failure(listOf(ProblemAtIndex(index, MissingLiteral(text))), text)
            }
        }
    }

    private object Success : ParsedArguments.Success<Unit> {
        override fun value() {
        }
    }

    private class Failure(val problems: List<ProblemAtIndex>, val text: String) : ParsedArguments.Failure {
        override fun problems(): List<ProblemAtIndex> {
            return problems
        }

        override fun claimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            return if (!args.firstIsOption()) {
                ParsedArguments.Unrecognized(1, ProblemAtIndex(args.index, UnexpectedArgument(args.first(), text)))
            } else {
                null
            }
        }
    }
}
