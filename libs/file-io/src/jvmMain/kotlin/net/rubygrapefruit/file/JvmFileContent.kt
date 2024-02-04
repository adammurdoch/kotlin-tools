package net.rubygrapefruit.file

import net.rubygrapefruit.io.stream.*
import java.io.RandomAccessFile

internal class JvmFileContent(
    private val file: RandomAccessFile
) : FileContent, ReadStream, WriteStream, AutoCloseable {
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

    override fun close() {
        file.close()
    }
}