package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.suffixed
import net.rubygrapefruit.parse.general.endOfInput

internal open class DefaultPushParser<CONTEXT, IN : ContextualInput<CONTEXT, *>, OUT>(
    parser: Parser<*, OUT>,
    private val failureFormatter: (CONTEXT, String) -> String
) {
    private val end = ValueReceivingContinuation<IN, OUT>()
    private var state: ParseState<IN> = parser.start()
    private var failedChoice: ExpectationProvider? = null
    private var failedChoiceIndex = 0
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
                        is PullParser.Failed -> {
                            val effectiveFailure = if (failedChoice != null) {
                                PullParser.Failed.merged(listOf(PullParser.Failed(failedChoiceIndex, failedChoice!!), result))
                            } else {
                                result
                            }
                            state = effectiveFailure
                        }

                        is PullParser.Matched -> state = result
                        is PullParser.RequireMore -> {
                            input.advance(result.advance)
                            if (result.failedChoice != null) {
                                if (result.advance == 0 && failedChoiceIndex == 0 && failedChoice != null) {
                                    failedChoice = ExpectationProvider.oneOf(failedChoice!!, result.failedChoice)
                                } else {
                                    failedChoice = result.failedChoice
                                    failedChoiceIndex = 0
                                }
                            } else {
                                failedChoiceIndex -= result.advance
                            }
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
        return all.compile<IN, OUT>().start(end)
    }
}

private class ValueReceivingContinuation<IN, OUT> : ParseContinuation<IN, OUT> {
    private var value: ValueProvider<OUT>? = null

    override val matches: Boolean
        get() = true

    fun get(): OUT {
        return value!!.get()
    }

    override fun next(length: Int, value: ValueProvider<OUT>): PullParser<IN> {
        this.value = value
        return ParseContinuation.end<IN, OUT>().next(length, value)
    }
}