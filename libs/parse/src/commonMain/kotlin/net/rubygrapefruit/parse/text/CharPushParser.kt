package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.PushParser

interface CharPushParser<OUT> : PushParser<CharFailureContext, OUT> {
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