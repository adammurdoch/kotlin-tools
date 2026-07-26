package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.PushParser

/**
 * A parser that takes binary input and produces a result of type [OUT].
 */
interface TextPushParser<OUT> : PushParser<TextFailureContext, OUT> {
    /**
     * Signals that more input is available.
     *
     * If the parsing fails, returns the failure. Returns `null` to signal that more input is required.
     */
    fun input(chars: CharArray): ParseResult.Fail<TextFailureContext>? {
        return input(chars, 0, chars.size)
    }

    /**
     * Signals that more input is available.
     *
     * If the parsing fails, returns the failure. Returns `null` to signal that more input is required.
     */
    fun input(chars: CharArray, offset: Int, count: Int): ParseResult.Fail<TextFailureContext>?

    /**
     * Parses input supplied by the given function. Calls the function when more input is required.
     *
     * The function should return the number of chars written into the array or -1 when the end of its input has been reached.
     * The function should only write into the specified segment of the array.
     *
     * This function does not call [endOfInput] when the end of the supplied input is reached, so that this parser can continue to receive more input.
     */
    fun takeFrom(reader: (buffer: CharArray, offset: Int, max: Int) -> Int): ParseResult.Fail<TextFailureContext>?
}