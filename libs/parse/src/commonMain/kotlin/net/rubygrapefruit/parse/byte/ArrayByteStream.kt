package net.rubygrapefruit.parse.byte

internal class ArrayByteStream(val bytes: ByteArray) : AdvancingByteStream {
    private var pos = 0

    override val available: Int
        get() = bytes.size - pos

    override val finished: Boolean
        get() = true

    override fun get(index: Int): Byte {
        return bytes[pos + index]
    }

    override fun advance(count: Int) {
        pos += count
    }

    override fun posAt(index: Int): BytePosition {
        return BytePosition(index + pos)
    }
}