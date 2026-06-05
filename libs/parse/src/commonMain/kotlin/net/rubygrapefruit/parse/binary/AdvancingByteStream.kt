package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.stream.ContextualInput
import net.rubygrapefruit.parse.Position

internal interface AdvancingByteStream : ByteStream, ContextualInput<BinaryFailureContext, BytePosition> {
    override fun contextAt(position: Position): BinaryFailureContext {
        val index = position - this.position
        val found = if (index >= available) {
            "end of input"
        } else {
            format(get(index))
        }
        return BinaryStreamContext(posAt(index), found)
    }

    fun posAt(index: Int): BytePosition

    private class BinaryStreamContext(override val position: BytePosition, override val found: String) : BinaryFailureContext {
        override fun toString(): String {
            return "{context offset=${position.position}}"
        }
    }
}
