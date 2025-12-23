package net.rubygrapefruit.bytecode

import java.io.DataInputStream
import java.io.InputStream

internal class StreamBackedDecoder(
    input: InputStream
) : Decoder {
    private val stream = CountingStream(input)
    private val input = DataInputStream(stream)

    override val offset: Long
        get() = stream.count

    override fun u1(): UByte {
        return input.readByte().toUByte()
    }

    override fun u2(): UInt {
        return input.readShort().toUInt()
    }

    override fun u4(): UInt {
        return input.readInt().toUInt()
    }

    override fun i4(): Int {
        return input.readInt()
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

    private class CountingStream(val stream: InputStream) : InputStream() {
        var count: Long = 0

        override fun read(): Int {
            val b = stream.read()
            if (b >= 0) {
                count++
            }
            return b
        }

        override fun read(b: ByteArray): Int {
            val nread = stream.read(b)
            if (nread >= 0) {
                count += nread
            }
            return nread
        }

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            val nread = stream.read(b, off, len)
            if (nread >= 0) {
                count += nread
            }
            return nread
        }

        override fun skip(n: Long): Long {
            val nskipped = stream.skip(n)
            if (nskipped > 0) {
                count += nskipped
            }
            return nskipped
        }
    }
}