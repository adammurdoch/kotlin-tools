package net.rubygrapefruit.file

import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import net.rubygrapefruit.io.readFrom
import net.rubygrapefruit.io.writeAtMostTo
import java.io.RandomAccessFile

@OptIn(UnsafeIoApi::class)
internal class JvmFileContent(
    private val owner: JvmRegularFile,
    private val file: RandomAccessFile
) : FileContent, RawSource, RawSink, AutoCloseable {
    override val currentPosition: Long
        get() = file.filePointer

    override fun seek(position: Long) {
        file.seek(position)
    }

    override fun seekToEnd(): Long {
        val length = file.length()
        file.seek(length)
        return length
    }

    override fun length(): Long {
        return file.length()
    }

    override val sink: RawSink
        get() = this

    override val source: RawSource
        get() = this

    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        val ncopied = sink.writeAtMostTo(byteCount) { buffer, startIndex, count ->
            val nread = try {
                file.read(buffer, startIndex, count)
            } catch (e: Exception) {
                throw readFile<Any>(owner, cause = e).failure
            }
            if (nread < 0) 0 else nread
        }
        return if (ncopied == 0) -1 else ncopied.toLong()
    }

    override fun write(source: Buffer, byteCount: Long) {
        source.readFrom(byteCount) { buffer, startIndex, count ->
            file.write(buffer, startIndex, count)
        }
    }

    override fun flush() {
    }

    override fun close() {
        file.close()
    }
}