package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.PushParser

interface CharPushParser<OUT> : PushParser<CharPosition, OUT> {
    /**
     * Signals that more input is available.
     */
    fun input(chars: CharArray)
}