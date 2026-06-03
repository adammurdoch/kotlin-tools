package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Offset
import net.rubygrapefruit.parse.minus
import net.rubygrapefruit.parse.plus

internal class ArrayByteStream(val bytes: ByteArray) : AdvancingByteStream {
    override var offset = Offset.Zero
        private set

    override val available: Int
        get() = bytes.size - offset

    override val finished: Boolean
        get() = true

    override fun get(index: Int): Byte {
        return bytes[index + offset]
    }

    override fun get(start: Int, end: Int): ByteArray {
        return bytes.copyOfRange(start + offset, end + offset)
    }

    override fun advance(count: Int) {
        offset += count
    }

    override fun posAt(index: Int): BytePosition {
        return BytePosition(offset + index)
    }
}