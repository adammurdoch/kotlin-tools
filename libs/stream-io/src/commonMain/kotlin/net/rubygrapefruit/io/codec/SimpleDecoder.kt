package net.rubygrapefruit.io.codec

import net.rubygrapefruit.io.stream.ReadStream

/**
 * Uses big-endian, fixed width encoding
 */
class SimpleDecoder(
    private val stream: ReadStream
) : Decoder {
    private val buffer = ByteArray(4)

    override fun ushort(): UShort {
        read(buffer, 2)
        return (buffer[0].toUShort().and(0xffu).rotateLeft(8))
            .or(buffer[1].toUShort().and(0xffu))
    }

    override fun int(): Int {
        read(buffer, 4)
        return (buffer[0].toInt().and(0xff).shl(24))
            .or(buffer[1].toInt().and(0xff).shl(16))
            .or(buffer[2].toInt().and(0xff).shl(8))
            .or(buffer[3].toInt().and(0xff))
    }

    override fun string(): String {
        val length = int()
        val buffer = ByteArray(length)
        read(buffer, length)
        return buffer.decodeToString()
    }

    private fun read(buffer: ByteArray, count: Int) {
        stream.readFully(buffer, 0, count)
    }
}