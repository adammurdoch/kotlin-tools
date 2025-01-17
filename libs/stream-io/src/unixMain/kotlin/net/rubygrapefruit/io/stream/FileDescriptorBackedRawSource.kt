@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import net.rubygrapefruit.error.UnixErrorCode
import net.rubygrapefruit.io.isNotFile
import net.rubygrapefruit.io.writeAtMostTo
import platform.posix.EISDIR
import platform.posix.errno
import platform.posix.read

@OptIn(UnsafeIoApi::class)
class FileDescriptorBackedRawSource(private val streamSource: StreamSource, private val descriptor: ReadDescriptor) : RawSource {
    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        val ncopied = sink.writeAtMostTo(byteCount) { buffer, startIndex, count ->
            val nread = read(descriptor.descriptor, buffer.refTo(startIndex), count.convert()).convert<Int>()
            if (nread < 0) {
                if (errno == EISDIR) {
                    throw isNotFile(streamSource)
                } else {
                    throw streamSource.readFailed(UnixErrorCode.last())
                }
            }
            nread
        }
        return if (ncopied == 0) -1 else ncopied.toLong()
    }

    override fun close() {
        platform.posix.close(descriptor.descriptor)
    }
}