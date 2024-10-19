@file:OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import net.rubygrapefruit.io.stream.FileDescriptorBackedRawSink
import net.rubygrapefruit.io.stream.FileDescriptorBackedRawSource
import net.rubygrapefruit.io.stream.ReadDescriptor
import net.rubygrapefruit.io.stream.WriteDescriptor
import platform.posix.*

internal class UnixFileContent(
    private val fileSource: FileSource,
    private val des: Int
) : FileContent, AutoCloseable {
    override val currentPosition: Long
        get() {
            val pos = lseek(des, 0, SEEK_CUR)
            return if (pos < 0) {
                throw NativeException("Could not query current position.")
            } else {
                pos
            }
        }

    override val sink: RawSink
        get() = FileDescriptorBackedRawSink(fileSource, WriteDescriptor(des))

    override val source: RawSource
        get() = FileDescriptorBackedRawSource(fileSource, ReadDescriptor(des))

    override fun length(): Long {
        return memScoped {
            val stat = alloc<stat>()

            if (fstat(des, stat.ptr) != 0) {
                throw NativeException("Could not stat file")
            }

            stat.st_size
        }
    }

    override fun seek(position: Long) {
        val pos = lseek(des, position, SEEK_SET)
        if (pos < 0) {
            throw NativeException("Could not set current position.")
        }
    }

    override fun seekToEnd(): Long {
        val pos = lseek(des, 0, SEEK_END)
        if (pos < 0) {
            throw NativeException("Could not set current position.")
        }
        return pos
    }

    override fun close() {
        close(des)
    }
}