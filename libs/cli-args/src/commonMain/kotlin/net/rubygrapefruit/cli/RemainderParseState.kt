package net.rubygrapefruit.cli

internal class RemainderParseState(
    private val target: RemainderParameter, private val context: ParseContext, private val required: Boolean
) : ParseState {
    override fun parseNextValue(args: List<String>): ParseState.Result {
        val result = args.toList()
        return ParseState.Success(args.size) {
            target.values(result)
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return if (required) {
            missingParameter(target.name, context)
        } else {
            ParseState.FinishSuccess {
                target.values(emptyList())
            }
        }
    }
}