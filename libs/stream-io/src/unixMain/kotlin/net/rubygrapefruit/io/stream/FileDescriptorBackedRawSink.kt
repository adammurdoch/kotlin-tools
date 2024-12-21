@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.UnsafeIoApi
import net.rubygrapefruit.error.UnixErrorCode
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.readFrom
import platform.posix.write

@OptIn(UnsafeIoApi::class)
class FileDescriptorBackedRawSink(private val streamSource: StreamSource, private val descriptor: WriteDescriptor) : RawSink {
    override fun write(source: Buffer, byteCount: Long) {
        memScoped {
            source.readFrom(byteCount) { buffer, startIndex, count ->
                val bytesWritten = write(descriptor.descriptor, buffer.refTo(startIndex), count.convert()).convert<Int>()
                if (bytesWritten < 0) {
                    throw IOException("Could not write to ${streamSource.displayName}.", UnixErrorCode.last())
                }
                bytesWritten
            }
        }
    }

    override fun flush() {
    }

    override fun close() {
        platform.posix.close(descriptor.descriptor)
    }
}