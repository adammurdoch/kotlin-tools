@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.io.unsafe.withData
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.UnixErrorCode
import platform.posix.write

@OptIn(UnsafeIoApi::class)
class FileDescriptorBackedRawSink(private val fileSource: StreamSource, private val descriptor: WriteDescriptor) : RawSink {
    override fun write(source: Buffer, byteCount: Long) {
        memScoped {
            UnsafeBufferOperations.forEachSegment(source) { context, segment ->
                context.withData(segment) { buffer, startIndex, endIndex ->
                    var pos = startIndex
                    var remaining = endIndex - startIndex
                    while (remaining > 0) {
                        val bytesWritten = write(descriptor.descriptor, buffer.refTo(pos), remaining.convert()).convert<Int>()
                        if (bytesWritten < 0) {
                            throw IOException("Could not write to ${fileSource.displayName}.", UnixErrorCode.last())
                        }
                        pos += bytesWritten
                        remaining -= bytesWritten
                    }
                }
            }
        }
    }

    override fun flush() {
    }

    override fun close() {
        platform.posix.close(descriptor.descriptor)
    }
}