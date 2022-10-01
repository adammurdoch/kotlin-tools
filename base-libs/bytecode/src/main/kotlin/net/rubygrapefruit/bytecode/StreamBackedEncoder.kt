package net.rubygrapefruit.bytecode

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.OutputStream

internal class StreamBackedEncoder(
    stream: OutputStream
) : Encoder {
    private val output = DataOutputStream(stream)

    override fun writing(builder: Encoder.() -> Unit): ByteArray {
        val stream = ByteArrayOutputStream()
        val nested = StreamBackedEncoder(stream)
        builder(nested)
        nested.flush()
        return stream.toByteArray()
    }

    override fun string(string: String) {
        output.writeUTF(string)
    }

    override fun bytes(bytes: ByteArray) {
        output.write(bytes)
    }

    override fun u1(value: UByte) {
        output.writeByte(value.toInt())
    }

    override fun u2(value: UInt) {
        output.writeShort(value.toInt())
    }

    override fun u4(value: UInt) {
        output.writeInt(value.toInt())
    }

    override fun i4(value: Int) {
        output.writeInt(value)
    }

    fun flush() {
        output.flush()
    }
}