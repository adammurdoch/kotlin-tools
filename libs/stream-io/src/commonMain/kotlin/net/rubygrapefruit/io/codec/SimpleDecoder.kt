package net.rubygrapefruit.io.codec

import kotlinx.io.Source
import kotlinx.io.readByteArray

/**
 * Uses big-endian, fixed width encoding
 */
internal class SimpleDecoder(
    private val source: Source
) : Decoder {
    override fun hasMore(): Boolean {
        return !source.exhausted()
    }

    override fun ubyte(): UByte {
        return source.readByte().toUByte()
    }

    override fun ushort(): UShort {
        return (source.readByte().toUShort().and(0xffu).rotateLeft(8))
            .or(source.readByte().toUShort().and(0xffu))
    }

    override fun byte(): Byte {
        return source.readByte()
    }

    override fun int(): Int {
        return (source.readByte().toInt().and(0xff).shl(24))
            .or(source.readByte().toInt().and(0xff).shl(16))
            .or(source.readByte().toInt().and(0xff).shl(8))
            .or(source.readByte().toInt().and(0xff))
    }

    override fun long(): Long {
        return (source.readByte().toLong().and(0xff).shl(56))
            .or(source.readByte().toLong().and(0xff).shl(48))
            .or(source.readByte().toLong().and(0xff).shl(40))
            .or(source.readByte().toLong().and(0xff).shl(32))
            .or(source.readByte().toLong().and(0xff).shl(24))
            .or(source.readByte().toLong().and(0xff).shl(16))
            .or(source.readByte().toLong().and(0xff).shl(8))
            .or(source.readByte().toLong().and(0xff))
    }

    override fun bytes(): ByteArray {
        val length = int()
        return source.readByteArray(length)
    }

    override fun string(): String {
        val length = int()
        return source.readByteArray(length).decodeToString()
    }
}