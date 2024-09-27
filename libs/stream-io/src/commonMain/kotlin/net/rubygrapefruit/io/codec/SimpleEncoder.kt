package net.rubygrapefruit.io.codec

import kotlinx.io.Buffer
import kotlinx.io.RawSink

/**
 * Uses big-endian, fixed width encoding
 */
internal class SimpleEncoder(
    private val sink: RawSink
) : Encoder {
    private val buffer = Buffer()

    override fun ubyte(value: UByte): Encoder {
        buffer.clear()
        buffer.writeByte(value.toByte())
        sink.write(buffer, 1)
        return this
    }

    override fun ushort(value: UShort): Encoder {
        buffer.clear()
        buffer.writeByte(value.rotateRight(8).toByte())
        buffer.writeByte(value.toByte())
        sink.write(buffer, 2)
        return this
    }

    override fun int(value: Int): Encoder {
        buffer.clear()
        buffer.writeByte(value.rotateRight(24).toByte())
        buffer.writeByte(value.rotateRight(16).toByte())
        buffer.writeByte(value.rotateRight(8).toByte())
        buffer.writeByte(value.toByte())
        sink.write(buffer, 4)
        return this
    }

    override fun long(value: Long): Encoder {
        buffer.clear()
        buffer.writeByte(value.rotateRight(56).toByte())
        buffer.writeByte(value.rotateRight(48).toByte())
        buffer.writeByte(value.rotateRight(40).toByte())
        buffer.writeByte(value.rotateRight(32).toByte())
        buffer.writeByte(value.rotateRight(24).toByte())
        buffer.writeByte(value.rotateRight(16).toByte())
        buffer.writeByte(value.rotateRight(8).toByte())
        buffer.writeByte(value.toByte())
        sink.write(buffer, 8)
        return this
    }

    override fun string(value: String): Encoder {
        val bytes = value.encodeToByteArray()
        int(bytes.size)
        buffer.clear()
        buffer.write(bytes)
        sink.write(buffer, buffer.size)
        return this
    }
}