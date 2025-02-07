package net.rubygrapefruit.cli

internal class ActionParseState(
    private val context: ParseContext,
    namedParameters: List<NonPositional>,
    positionalParameters: List<Positional>
) : AbstractCollectingParseState() {
    private var named: ParseState = OneOfParseState(namedParameters.map { it.start(context) })
    private var positional: ParseState = SequenceParseState(positionalParameters.map { it.start(context) })
    private var parsedContext = context

    override fun parseNextValue(args: List<String>): ParseState.Result {
        val namedResult = named.parseNextValue(args)
        when (namedResult) {
            is ParseState.Success -> {
                named = NothingParseState
                return ParseState.Continue(namedResult.consumed, this, namedResult.hint, namedResult.apply)
            }

            is ParseState.Failure -> return namedResult

            is ParseState.Continue -> {
                named = namedResult.state
                return ParseState.Continue(namedResult.consumed, this, namedResult.hint, namedResult.apply)
            }

            is ParseState.Nothing -> {
                // Try a positional parameter
            }
        }

        val positionalResult = positional.parseNextValue(args)
        return when (positionalResult) {
            is ParseState.Success -> {
                positional = NothingParseState
                parsedContext = positionalResult.context ?: context
                ParseState.Continue(positionalResult.consumed, this, positionalResult.hint, positionalResult.apply)
            }

            is ParseState.Failure -> positionalResult

            is ParseState.Continue -> {
                positional = positionalResult.state
                ParseState.Continue(positionalResult.consumed, this, positionalResult.hint, positionalResult.apply)
            }

            is ParseState.Nothing -> {
                // No named or positional parameter, finish this action
                val finishResult = endOfInput()
                when (finishResult) {
                    is ParseState.FinishFailure -> finishResult.toResult()
                    is ParseState.FinishSuccess -> ParseState.Success(0, null, parsedContext, finishResult.apply)
                }
            }
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        return finish(listOf(named, positional))
    }

    private object NothingParseState : ParseState {
        override fun parseNextValue(args: List<String>): ParseState.Result {
            return ParseState.Nothing
        }

        override fun endOfInput(): ParseState.FinishResult {
            return ParseState.FinishSuccess {}
        }
    }
}