package net.rubygrapefruit.parse.byte

internal class ArrayByteStream(val bytes: ByteArray) : ByteStream {
    override val available: Int
        get() = bytes.size

    override val finished: Boolean
        get() = true

    override fun get(index: Int): Byte {
        return bytes[index]
    }
}