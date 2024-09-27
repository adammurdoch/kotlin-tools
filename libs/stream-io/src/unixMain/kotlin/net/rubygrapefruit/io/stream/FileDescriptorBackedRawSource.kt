@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import net.rubygrapefruit.io.UnixErrorCode
import platform.posix.EISDIR
import platform.posix.errno
import platform.posix.read
import kotlin.math.min

@OptIn(UnsafeIoApi::class)
class FileDescriptorBackedRawSource(private val fileSource: Source, private val descriptor: ReadDescriptor) : RawSource {
    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        val ncopied = UnsafeBufferOperations.writeToTail(sink, 1) { buffer, startIndex, endIndex ->
            val count = min(byteCount.toInt(), endIndex - startIndex)
            val nread = read(descriptor.descriptor, buffer.refTo(startIndex), count.convert()).convert<Int>()
            if (nread < 0) {
                if (errno == EISDIR) {
                    throw ReadFailed.isNotFile(fileSource).exception
                } else {
                    throw ReadFailed.readFile(fileSource, UnixErrorCode.last()).exception
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