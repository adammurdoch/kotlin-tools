package net.rubygrapefruit.file

import net.rubygrapefruit.io.stream.*
import java.io.RandomAccessFile

class JvmFileContent(
    private val file: RandomAccessFile
) : FileContent, ReadStream, WriteStream {
    override val currentPosition: UInt
        get() = file.filePointer.toUInt()

    override fun seek(position: UInt) {
        file.seek(position.toLong())
    }

    override fun seekToEnd() {
        file.seek(file.length())
    }

    override fun length(): UInt {
        return file.length().toUInt()
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
}