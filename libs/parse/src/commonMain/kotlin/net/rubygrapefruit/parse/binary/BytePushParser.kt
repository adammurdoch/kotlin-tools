package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.PushParser

interface BytePushParser<OUT> : PushParser<ByteFailureContext, OUT> {
    /**
     * Signals that more input is available.
     */
    fun input(bytes: ByteArray)
}