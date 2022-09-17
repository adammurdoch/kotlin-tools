package net.rubygrapefruit.bytecode

import java.io.DataInputStream
import java.io.InputStream

internal class StreamBackedDecoder(
    input: InputStream
) : Decoder {
    private val input = DataInputStream(input)

    override fun u1(): UByte {
        return input.readByte().toUByte()
    }

    override fun u2(): UInt {
        return input.readShort().toUInt()
    }

    override fun u4(): UInt {
        return input.readInt().toUInt()
    }

    override fun string(): String {
        return input.readUTF()
    }

    override fun skip(count: Int) {
        var remaining = count
        while (remaining > 0) {
            val skipped = input.skipBytes(remaining)
            if (skipped <= 0) {
                throw IllegalArgumentException("Unexpected end of stream")
            }
            remaining -= skipped
        }
    }
}