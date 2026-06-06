package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.suffixed
import net.rubygrapefruit.parse.general.endOfInput
import net.rubygrapefruit.parse.stream.ContextualInput

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
                is PullParser.Failed -> return mapFailed(input)

                is PullParser -> {
                    val result = currentState.parse(input, input.available)
                    when (result) {
                        is PullParser.Matched -> state = result

                        is PullParser.Failed -> {
                            collectFailedChoices(result.failures, input)
                            state = result
                        }

                        is PullParser.RequireMore -> {
                            collectFailedChoices(result.failedChoices, input)
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

    fun endOfInput(input: IN): ParseResult<CONTEXT, OUT> {
        inputAvailable(input)

        val finalState = state
        return when (finalState) {
            is PullParser.Matched -> ParseResult.Success(end.get())
            is PullParser.Failed -> mapFailed(input)!!
            is PullParser -> throw IllegalStateException("Expected parsing to be finished, but is $finalState")
        }
    }

    fun maybeFailed(): ParseResult.Fail<CONTEXT>? {
        return failure
    }

    private fun collectFailedChoices(failures: List<PullParser.Failure>, input: IN) {
        for (failure in failures) {
            val failurePosition = failure.position(input.position)
            if (failurePosition == failedChoicePosition) {
                failedChoice = ExpectationProvider.oneOf(failedChoice, failure.expected)
            } else if (failurePosition > failedChoicePosition) {
                failedChoice = failure.expected
                failedChoicePosition = failurePosition
            }
        }
    }

    private fun mapFailed(input: IN): ParseResult.Fail<CONTEXT>? {
        if (failure != null) {
            return failure
        }
        val context = input.contextAt(failedChoicePosition)
        return if (context == null) {
            // Context is not yet available (e.g. failure is on current line and end-of-line not yet available) so wait for more input
            null
        } else {
            val mapped = ParseResult.Fail<CONTEXT>(context, failedChoice.expectation().format(), failureFormatter)
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

    override fun matched(input: IN, advance: Int, length: Int, value: ValueProvider<OUT>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<IN> {
        this.value = value
        return ParseContinuation.end<IN, OUT>().matched(input, advance, length, value, failedChoices)
    }

    override fun <T> selected(advance: Int, parser: PullParser<T>, failedChoices: List<PullParser.Failure>): PullParser.RequireMore<T> {
        return ParseContinuation.end<IN, OUT>().selected(advance, parser, failedChoices)
    }
}