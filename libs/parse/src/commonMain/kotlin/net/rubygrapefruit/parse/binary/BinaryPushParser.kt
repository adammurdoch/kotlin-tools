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
}