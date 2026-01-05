package net.rubygrapefruit.parse.byte

internal class ByteArrayStream(val bytes: ByteArray) : ByteStream {
    override val length: Int
        get() = bytes.size

    override fun next(index: Int): Byte {
        return bytes[index]
    }
}