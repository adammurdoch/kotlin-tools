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
            // Keep going to allow the value to be overridden by a later flag
            ParseState.Continue(1, this, null) {}
        } else if (disableFlags.contains(arg)) {
            value = false
            // Keep going to allow the value to be overridden by a later flag
            ParseState.Continue(1, this, null) {}
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