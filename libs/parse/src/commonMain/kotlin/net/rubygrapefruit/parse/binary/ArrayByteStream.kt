package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Position
import net.rubygrapefruit.parse.minus
import net.rubygrapefruit.parse.plus

internal class ArrayByteStream(val bytes: ByteArray) : AdvancingByteStream {
    override var position = Position.Zero
        private set

    override val available: Int
        get() = bytes.size - position

    override val finished: Boolean
        get() = true

    override fun get(index: Int): Byte {
        return bytes[index + position]
    }

    override fun get(start: Int, end: Int): ByteArray {
        return bytes.copyOfRange(start + position, end + position)
    }

    override fun advance(count: Int) {
        position += count
    }

    override fun posAt(index: Int): BytePosition {
        return BytePosition(position + index)
    }
}