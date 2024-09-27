package net.rubygrapefruit.io.codec

import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.readString

/**
 * Uses big-endian, fixed width encoding
 */
internal class SimpleDecoder(
    private val source: RawSource
) : Decoder {
    private val buffer = Buffer()

    override fun ubyte(): UByte {
        read(buffer, 1)
        return buffer[0].toUByte()
    }

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

    override fun long(): Long {
        read(buffer, 8)
        return (buffer[0].toLong().and(0xff).shl(56))
            .or(buffer[1].toLong().and(0xff).shl(48))
            .or(buffer[2].toLong().and(0xff).shl(40))
            .or(buffer[3].toLong().and(0xff).shl(32))
            .or(buffer[4].toLong().and(0xff).shl(24))
            .or(buffer[5].toLong().and(0xff).shl(16))
            .or(buffer[6].toLong().and(0xff).shl(8))
            .or(buffer[7].toLong().and(0xff))
    }

    override fun string(): String {
        val length = int()
        read(buffer, length)
        return buffer.readString()
    }

    private fun read(buffer: Buffer, count: Int) {
        buffer.clear()
        buffer.write(source, count.toLong())
    }
}