@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import net.rubygrapefruit.io.stream.*
import platform.posix.*

internal class UnixFileContent(
    source: UnixRegularFile.FileSource,
    private val des: Int
) : FileContent {
    override val currentPosition: UInt
        get() {
            val pos = lseek(des, 0, SEEK_CUR)
            return if (pos < 0) {
                throw NativeException("Could not query current position.")
            } else {
                pos.toUInt()
            }
        }

    override val writeStream: WriteStream = FileDescriptorBackedWriteStream(source, WriteDescriptor(des))

    override val readStream: ReadStream = FileDescriptorBackedReadStream(source, ReadDescriptor(des))

    override fun length(): UInt {
        return memScoped {
            val stat = alloc<stat>()

            if (fstat(des, stat.ptr) != 0) {
                throw NativeException("Could not stat file")
            }

            stat.st_size.convert()
        }
    }

    override fun seek(position: UInt) {
        val pos = lseek(des, position.convert(), SEEK_SET)
        if (pos < 0) {
            throw NativeException("Could not set current position.")
        }
    }

    override fun seekToEnd() {
        val pos = lseek(des, 0, SEEK_END)
        if (pos < 0) {
            throw NativeException("Could not set current position.")
        }
    }
}