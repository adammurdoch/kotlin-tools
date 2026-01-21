package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.AdvancingInput

internal interface AdvancingByteStream : ByteStream, AdvancingInput<BytePosition> {
    fun contextAt(index: Int): ByteFailureContext {
        return ByteStreamContext(posAt(index))
    }

    private class ByteStreamContext(override val position: BytePosition) : ByteFailureContext {
        override fun toString(): String {
            return "{context offset=${position.offset}}"
        }
    }
}
