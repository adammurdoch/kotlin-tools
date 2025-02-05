package net.rubygrapefruit.cli

internal class ActionParseState<T : Action>(
    private val context: ParseContext,
    private val action: T,
    namedParameters: List<NonPositional>,
    positionalParameters: List<Positional>,
    private val consumer: (T) -> Unit
) : AbstractCollectingParseState() {
    private var named: ParseState = OneOfParseState(namedParameters.map { it.start(context) })
    private var positional: ParseState = SequenceParseState(positionalParameters.map { it.start(context) })

    override fun parseNextValue(args: List<String>): ParseState.Result {
        val namedResult = named.parseNextValue(args)
        when (namedResult) {
            is ParseState.Success -> {
                collect(namedResult)
                named = NothingParseState
                return ParseState.Continue(namedResult.consumed, this)
            }

            is ParseState.Failure -> return namedResult

            is ParseState.Continue -> {
                named = namedResult.state
                return ParseState.Continue(namedResult.consumed, this)
            }

            is ParseState.Nothing -> {
                // Try a positional parameter
            }
        }

        val positionalResult = positional.parseNextValue(args)
        return when (positionalResult) {
            is ParseState.Success -> {
                collect(positionalResult)
                positional = NothingParseState
                ParseState.Continue(positionalResult.consumed, this)
            }

            is ParseState.Failure -> positionalResult

            is ParseState.Continue -> {
                positional = positionalResult.state
                ParseState.Continue(positionalResult.consumed, this)
            }

            is ParseState.Nothing -> {
                // No named or positional parameter, finish this action
                val finishResult = endOfInput()
                when (finishResult) {
                    is ParseState.FinishFailure -> finishResult.toResult()
                    is ParseState.FinishSuccess -> ParseState.Success(0, null, finishResult.apply)
                }
            }
        }
    }

    override fun endOfInput(): ParseState.FinishResult {
        collect {
            consumer(action)
        }
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