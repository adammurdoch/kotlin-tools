package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultBooleanOption(private val flags: List<String>, private val names: List<String>) : AbstractParser<Boolean>(), BooleanOption {
    override fun required(): Parser<Unit> {
        return RequiredBooleanOption(flags, names)
    }

    override fun start(): ParseState<Boolean> {
        return State(flags, names)
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal {
        override fun writeTo(quoted: Boolean, output: Output) {
            output.write("[")
            writeChoices(false, output, flags) { flag ->
                output.write(flag)
            }
            output.write("]")
        }
    }

    private class State(flags: List<String>, names: List<String>) : OptionParseState<Boolean>(flags, names) {
        override fun matched(args: InputArgs): ParseState.Finished<Boolean> {
            return ParseState.Finished(1, ParseState.FixedResult(true))
        }

        override fun consumeBadlyFormed(args: InputArgs): Int {
            return 1
        }

        override fun finish(index: Int): ParseState.Result<Boolean> {
            return ParseState.FixedResult(false)
        }
    }
}