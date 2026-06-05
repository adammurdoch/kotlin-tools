package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.suffixed
import net.rubygrapefruit.parse.general.endOfInput

internal open class DefaultPushParser<CONTEXT, IN : ContextualInput<CONTEXT, *>, OUT>(
    parser: Parser<*, OUT>,
    private val failureFormatter: (CONTEXT, String) -> String
) {
    private val end = ValueReceivingContinuation<IN, OUT>()
    private var state: ParseState<IN> = parser.start()
    private var failedChoice: ExpectationProvider = Expectation.Nothing
    private var failedChoicePosition = Position.Zero
    private var failure: ParseResult.Fail<CONTEXT>? = null

    fun inputAvailable(input: IN): ParseResult.Fail<CONTEXT>? {
        while (true) {
            val currentState = state
            when (currentState) {
                is PullParser.Matched -> return null
                is PullParser.Failed -> return mapFailed(input, currentState)

                is PullParser -> {
                    val result = currentState.parse(input, input.available)
                    when (result) {
                        is PullParser.Failed -> state = mergeFailure(result, input)
                        is PullParser.Matched -> state = result
                        is PullParser.RequireMore -> {
                            collectFailedChoices(result, input)
                            input.advance(result.advance)
                            state = result.parser
                            if (input.available == 0 && !input.finished) {
                                return null
                            }
                        }
                    }
                }
            }
        }
    }

    private fun mergeFailure(failure: PullParser.Failed, input: IN): PullParser.Failed {
        val failurePosition = failure.position(input.position)
        return if (failurePosition > failedChoicePosition) {
            failure
        } else {
            PullParser.Failed(failure.index, ExpectationProvider.oneOf(failedChoice, failure.expected))
        }
    }

    private fun collectFailedChoices(result: PullParser.RequireMore<*>, input: IN) {
        if (result.failedChoice != null) {
            val currentFailure = failedChoice
            if (result.advance == 0 && failedChoicePosition == input.position) {
                failedChoice = ExpectationProvider.oneOf(currentFailure, result.failedChoice)
            } else {
                failedChoice = result.failedChoice
                failedChoicePosition = input.position + result.advance
            }
        }
    }

    fun endOfInput(input: IN): ParseResult<CONTEXT, OUT> {
        inputAvailable(input)

        val finalState = state
        return when (finalState) {
            is PullParser.Matched -> ParseResult.Success(end.get())
            is PullParser.Failed -> mapFailed(input, finalState)!!
            is PullParser -> throw IllegalStateException("Expected parsing to be finished, but is $finalState")
        }
    }

    fun maybeFailed(): ParseResult.Fail<CONTEXT>? {
        return failure
    }

    private fun mapFailed(input: IN, result: PullParser.Failed): ParseResult.Fail<CONTEXT>? {
        if (failure != null) {
            return failure
        }
        val context = input.contextAt(result.index)
        return if (context == null) {
            null
        } else {
            val mapped = ParseResult.Fail<CONTEXT>(context, result.expected.expectation().format(), failureFormatter)
            failure = mapped
            mapped
        }
    }

    private fun Parser<*, OUT>.start(): PullParser<IN> {
        val all = suffixed(this, endOfInput())
        return all.compile<IN, OUT>().start(Position.Zero, end)
    }
}

private class ValueReceivingContinuation<IN, OUT> : ParseContinuation<IN, OUT> {
    private var value: ValueProvider<OUT>? = null

    fun get(): OUT {
        return value!!.get()
    }

    override fun matched(advance: Int, commit: Int, length: Int, value: ValueProvider<OUT>, failedChoice: ExpectationProvider?): PullParser.RequireMore<IN> {
        this.value = value
        return ParseContinuation.end<IN, OUT>().matched(advance, commit, length, value, failedChoice)
    }

    override fun <T> selected(advance: Int, commit: Int, parser: PullParser<T>, failedChoice: ExpectationProvider?): PullParser.RequireMore<T> {
        return ParseContinuation.end<IN, OUT>().selected(advance, commit, parser, failedChoice)
    }
}