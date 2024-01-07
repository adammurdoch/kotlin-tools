package net.rubygrapefruit.io.codec

import net.rubygrapefruit.io.stream.WriteStream

/**
 * Uses big-endian, fixed width encoding
 */
internal class SimpleEncoder(
    private val stream: WriteStream
) : Encoder {
    private val buffer = ByteArray(8)

    override fun ushort(value: UShort): Encoder {
        buffer[0] = value.rotateRight(8).toByte()
        buffer[1] = value.toByte()
        stream.write(buffer, 0, 2)
        return this
    }

    override fun int(value: Int): Encoder {
        buffer[0] = value.rotateRight(24).toByte()
        buffer[1] = value.rotateRight(16).toByte()
        buffer[2] = value.rotateRight(8).toByte()
        buffer[3] = value.toByte()
        stream.write(buffer, 0, 4)
        return this
    }

    override fun long(value: Long): Encoder {
        buffer[0] = value.rotateRight(56).toByte()
        buffer[1] = value.rotateRight(48).toByte()
        buffer[2] = value.rotateRight(40).toByte()
        buffer[3] = value.rotateRight(32).toByte()
        buffer[4] = value.rotateRight(24).toByte()
        buffer[5] = value.rotateRight(16).toByte()
        buffer[6] = value.rotateRight(8).toByte()
        buffer[7] = value.toByte()
        stream.write(buffer, 0, 8)
        return this
    }

    override fun string(value: String): Encoder {
        val bytes = value.encodeToByteArray()
        int(bytes.size)
        stream.write(bytes)
        return this
    }
}