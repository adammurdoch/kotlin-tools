package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.PushParser

/**
 * A parser that takes binary input and produces a result of type [OUT].
 */
interface BinaryPushParser<OUT> : PushParser<BinaryFailureContext, OUT> {
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