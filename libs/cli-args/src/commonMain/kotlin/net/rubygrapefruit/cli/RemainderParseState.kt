package net.rubygrapefruit.cli

internal class RemainderParseState(private val target: RemainderParameter, private val required: Boolean) : ParseState {
    override fun parseNextValue(args: List<String>, context: ParseContext): ParseState.Result {
        val result = args.toList()
        return ParseState.Success(args.size) {
            target.values(result)
        }
    }

    override fun endOfInput(context: ParseContext): ParseState.FinishResult {
        return if (required) {
            ParseState.FinishFailure("Parameter '${target.name}' not provided")
        } else {
            ParseState.FinishSuccess {
                target.values(emptyList())
            }
        }
    }
}