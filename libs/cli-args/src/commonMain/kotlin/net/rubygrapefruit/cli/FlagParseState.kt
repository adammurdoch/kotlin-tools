package net.rubygrapefruit.cli

internal class FlagParseState(
    private val target: DefaultFlag,
    private val enableFlags: List<String>,
    private val disableFlags: List<String>,
    default: Boolean
) : ParseState {
    private var value: Boolean = default

    override fun parseNextValue(args: List<String>): ParseState.Result {
        val arg = args.first()
        return if (enableFlags.contains(arg)) {
            value = true
            ParseState.Continue(1, this)
        } else if (disableFlags.contains(arg)) {
            value = false
            ParseState.Continue(1, this)
        } else {
            ParseState.Nothing
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return ParseState.FinishSuccess {
            target.value(value)
        }
    }
}