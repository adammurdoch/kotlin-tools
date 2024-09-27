package net.rubygrapefruit.io.stream

import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.readByteArray

class CollectingRawSink : RawSink {
    private val buffer = Buffer()

    override fun write(source: Buffer, byteCount: Long) {
        buffer.write(source, byteCount)
    }

    fun toByteArray(): ByteArray {
        return buffer.readByteArray()
    }

    override fun flush() {
    }

    override fun close() {
        throw IllegalStateException()
    }
}