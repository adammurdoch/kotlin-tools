package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.AdvancingInput

internal interface AdvancingByteStream : ByteStream, AdvancingInput<BytePosition> {
    fun contextAt(index: Int): BinaryFailureContext {
        return BinaryStreamContext(posAt(index))
    }

    private class BinaryStreamContext(override val position: BytePosition) : BinaryFailureContext {
        override fun toString(): String {
            return "{context offset=${position.offset}}"
        }
    }
}
