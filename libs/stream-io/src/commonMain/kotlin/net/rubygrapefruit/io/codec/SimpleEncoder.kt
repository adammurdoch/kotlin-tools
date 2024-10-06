package net.rubygrapefruit.io.codec

import kotlinx.io.Sink

/**
 * Uses big-endian, fixed width encoding
 */
internal class SimpleEncoder(
    private val sink: Sink
) : Encoder {
    override fun ubyte(value: UByte): Encoder {
        sink.writeByte(value.toByte())
        return this
    }

    override fun ushort(value: UShort): Encoder {
        sink.writeByte(value.rotateRight(8).toByte())
        sink.writeByte(value.toByte())
        return this
    }

    override fun byte(value: Byte): Encoder {
        sink.writeByte(value)
        return this
    }

    override fun int(value: Int): Encoder {
        sink.writeByte(value.rotateRight(24).toByte())
        sink.writeByte(value.rotateRight(16).toByte())
        sink.writeByte(value.rotateRight(8).toByte())
        sink.writeByte(value.toByte())
        return this
    }

    override fun long(value: Long): Encoder {
        sink.writeByte(value.rotateRight(56).toByte())
        sink.writeByte(value.rotateRight(48).toByte())
        sink.writeByte(value.rotateRight(40).toByte())
        sink.writeByte(value.rotateRight(32).toByte())
        sink.writeByte(value.rotateRight(24).toByte())
        sink.writeByte(value.rotateRight(16).toByte())
        sink.writeByte(value.rotateRight(8).toByte())
        sink.writeByte(value.toByte())
        return this
    }

    override fun bytes(value: ByteArray): Encoder {
        int(value.size)
        sink.write(value)
        return this
    }

    override fun string(value: String): Encoder {
        val bytes = value.encodeToByteArray()
        int(bytes.size)
        sink.write(bytes)
        return this
    }
}