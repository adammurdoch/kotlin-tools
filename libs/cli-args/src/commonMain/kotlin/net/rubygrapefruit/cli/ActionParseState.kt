package net.rubygrapefruit.cli

internal class ActionParseState<T : Action>(private val context: ParseContext, private val action: T, private val consumer: (T) -> Unit) : ParseState {

    override fun parseNextValue(args: List<String>): ParseState.Result {
        TODO("Not yet implemented")
    }

    override fun endOfInput(): ParseState.FinishResult {
        TODO("Not yet implemented")
    }
}