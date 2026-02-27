package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.ContextualInput

internal interface AdvancingByteStream : ByteStream, ContextualInput<BinaryFailureContext, BytePosition> {
    override fun contextAt(index: Int): BinaryFailureContext {
        val found = if (index >= available) {
            "end of input"
        } else {
            format(get(index))
        }
        return BinaryStreamContext(posAt(index), found)
    }

    private class BinaryStreamContext(override val position: BytePosition, override val found: String) : BinaryFailureContext {
        override fun toString(): String {
            return "{context offset=${position.offset}}"
        }
    }
}
