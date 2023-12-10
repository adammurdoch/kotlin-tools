@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.UnixErrorCode
import platform.posix.write

class FileDescriptorBackedWriteStream(private val path: String, private val descriptor: FileDescriptor) : WriteStream {
    override fun write(bytes: ByteArray, offset: Int, count: Int) {
        memScoped {
            var pos = offset
            var remaining = count
            while (remaining > 0) {
                val bytesWritten = write(descriptor.descriptor, bytes.refTo(pos), remaining.convert()).convert<Int>()
                if (bytesWritten < 0) {
                    throw IOException("Could not write to file $path.", UnixErrorCode.last())
                }
                pos += bytesWritten
                remaining -= bytesWritten
            }
        }
    }
}