package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.PushParser

interface BytePushParser<OUT> : PushParser<ByteFailureContext, OUT> {
    /**
     * Signals that more input is available.
     */
    fun input(bytes: ByteArray) {
        input(bytes, 0, bytes.size)
    }

    /**
     * Signals that more input is available.
     */
    fun input(bytes: ByteArray, offset: Int, count: Int)
}