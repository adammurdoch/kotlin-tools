package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.PushParser

interface BytePushParser<OUT> : PushParser<OUT> {
    /**
     * Signals that more input is available.
     */
    fun input(bytes: ByteArray)
}