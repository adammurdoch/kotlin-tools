package net.rubygrapefruit.file

import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import net.rubygrapefruit.io.stream.*
import net.rubygrapefruit.io.write
import java.io.RandomAccessFile
import kotlin.math.min

@OptIn(UnsafeIoApi::class)
internal class JvmFileContent(
    private val file: RandomAccessFile
) : FileContent, ReadStream, WriteStream, RawSource, RawSink, AutoCloseable {
    override val currentPosition: Long
        get() = file.filePointer

    override fun seek(position: Long) {
        file.seek(position)
    }

    override fun seekToEnd(): Long {
        val length = file.length()
        file.seek(length)
        return length;
    }

    override fun length(): Long {
        return file.length()
    }

    override val writeStream: WriteStream
        get() = this

    override val readStream: ReadStream
        get() = this

    override val sink: RawSink
        get() = this

    override val source: RawSource
        get() = this

    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        val ncopied = UnsafeBufferOperations.writeToTail(sink, 1) { buffer, startIndex, endIndex ->
            val count = min(byteCount.toInt(), endIndex - startIndex)
            val nread = file.read(buffer, startIndex, count)
            if (nread < 0) 0 else nread
        }
        return if (ncopied == 0) -1 else ncopied.toLong()
    }

    override fun write(source: Buffer, byteCount: Long) {
        source.write(byteCount) { buffer, startIndex, count ->
            file.write(buffer, startIndex, count)
        }
    }

    override fun read(buffer: ByteArray, offset: Int, max: Int): ReadResult {
        val nread = file.read(buffer, offset, max)
        return if (nread < 0) {
            EndOfStream
        } else {
            ReadBytes(nread)
        }
    }

    override fun write(bytes: ByteArray, offset: Int, count: Int) {
        file.write(bytes, offset, count)
    }

    override fun flush() {
    }

    override fun close() {
        file.close()
    }
}