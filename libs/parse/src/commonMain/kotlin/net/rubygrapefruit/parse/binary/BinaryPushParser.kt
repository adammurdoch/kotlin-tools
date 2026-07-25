package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.PushParser

/**
 * A parser that takes binary input and produces a result of type [OUT].
 */
interface BinaryPushParser<OUT> : PushParser<BinaryFailureContext, OUT> {
    /**
     * Signals that more input is available.
     *
     * If the parsing fails, returns the failure. Returns `null` to signal that more input is required.
     */
    fun input(bytes: ByteArray): ParseResult.Fail<BinaryFailureContext>? {
        return input(bytes, 0, bytes.size)
    }

    /**
     * Signals that more input is available.
     *
     * If the parsing fails, returns the failure. Returns `null` to signal that more input is required.
     */
    fun input(bytes: ByteArray, offset: Int, count: Int): ParseResult.Fail<BinaryFailureContext>?

    /**
     * Parses input supplied by the given function. Calls the function when more input is required.
     * The function should return the number of bytes read into the array or -1 when the end of its input has been reached.
     *
     * This function does not call [endOfInput] when the end of the supplied input is reached, so that this parser can continue to receive more input.
     */
    fun takeFrom(reader: (buffer: ByteArray, offset: Int, max: Int) -> Int): ParseResult.Fail<BinaryFailureContext>?
}