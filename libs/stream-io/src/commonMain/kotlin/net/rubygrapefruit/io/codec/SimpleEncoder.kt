package net.rubygrapefruit.io.codec

import net.rubygrapefruit.io.stream.WriteStream

/**
 * Uses big-endian, fixed width encoding
 */
class SimpleEncoder(
    private val stream: WriteStream
) : Encoder {
    private val buffer = ByteArray(4)

    override fun ushort(value: UShort): Encoder {
        buffer[0] = value.rotateRight(8).toByte()
        buffer[1] = value.toByte()
        stream.write(buffer, 0, 2)
        return this
    }

    private fun int(value: Int): Encoder {
        buffer[0] = value.rotateRight(24).toByte()
        buffer[1] = value.rotateRight(16).toByte()
        buffer[2] = value.rotateRight(8).toByte()
        buffer[3] = value.toByte()
        stream.write(buffer, 0, 4)
        return this
    }

    override fun string(value: String): Encoder {
        val length = value.length
        int(length)
        val bytes = value.encodeToByteArray()
        stream.write(bytes)
        return this
    }
}