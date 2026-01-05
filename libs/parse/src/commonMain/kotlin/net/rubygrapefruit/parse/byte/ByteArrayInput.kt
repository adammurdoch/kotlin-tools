package net.rubygrapefruit.parse.byte

internal class ByteArrayInput(val bytes: ByteArray) : ByteInput {
    override val length: Int
        get() = bytes.size

    override fun next(index: Int): Byte {
        return bytes[index]
    }
}