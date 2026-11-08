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
        override fun writeTo(quoted: Boolean, output: Output) {
            writeChoices(quoted, output, flags) { flag ->
                output.write(flag)
            }
        }
    }

    private class State(flags: List<String>, names: List<String>) : OptionParseState<Unit>(flags, names) {

        override fun matched(args: InputArgs): ParseState.Finished<Unit> {
            return ParseState.Finished(1, ParseState.FixedResult(Unit))
        }

        override fun consumeBadlyFormed(args: InputArgs): Int {
            return 1
        }

        override fun finish(index: Int): ParseState.Result<Unit> {
            return if (suggested) {
                ParseState.FinalFailure(emptyList())
            } else {
                val longest = flags.maxBy { it.length }
                ParseState.FinalFailure(listOf(ProblemAtIndex(index, MissingOption(longest))))
            }
        }
    }
}