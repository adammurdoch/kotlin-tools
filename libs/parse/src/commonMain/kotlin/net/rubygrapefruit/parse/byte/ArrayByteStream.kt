package net.rubygrapefruit.parse.byte

internal class ArrayByteStream(val bytes: ByteArray) : ByteStream {
    override val length: Int
        get() = bytes.size

    override fun get(index: Int): Byte {
        return bytes[index]
    }
}