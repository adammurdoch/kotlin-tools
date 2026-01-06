package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.PushParser

interface CharPushParser<OUT> : PushParser<OUT> {
    /**
     * Signals that more input is available.
     */
    fun input(chars: CharArray)
}