package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultCommand(private val text: String) : AbstractParser<Unit>(), Parser<Unit> {
    override fun start(): ParseState<Unit> {
        return State(text)
    }

    override fun edges(dest: MutableList<ParseEdge>) {
        dest.add(CommandEdge(text))
    }

    override fun usage(): UsageInternal {
        return LiteralUsage()
    }

    private inner class LiteralUsage : UsageInternal {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write(text)
        }
    }

    private class CommandEdge(private val text: String) : ParseEdge.Positional {
        override fun next(args: InputArgs): ParseEdge.Transition {
            return if (args.first() == text) {
                ParseEdge.Transition.Matched(1, Finished(Unit))
            } else {
                ParseEdge.Transition.NotMatched
            }
        }
    }

    private class State(val text: String) : ParseState<Unit> {
        private var matched = false

        override val expectingOption: Boolean
            get() = false

        override val expectingPositional: Boolean
            get() = !matched

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
                listOf(ProblemAtIndex(index, MissingCommand(text)))
            }
        }

        override fun claimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            return if (!args.firstIsOption()) {
                suggested = true
                ParsedArguments.Unrecognized(1, ProblemAtIndex(args.index, UnexpectedArgument(args.first(), listOf(text))))
            } else {
                null
            }
        }
    }
}
