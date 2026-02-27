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
}