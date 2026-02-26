package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.PushParser

/**
 * A parser that takes binary input and produces a result of type [OUT].
 */
interface TextPushParser<OUT> : PushParser<TextFailureContext, OUT> {
    /**
     * Signals that more input is available.
     */
    fun input(chars: CharArray) {
        input(chars, 0, chars.size)
    }

    /**
     * Signals that more input is available.
     */
    fun input(chars: CharArray, offset: Int, count: Int)
}