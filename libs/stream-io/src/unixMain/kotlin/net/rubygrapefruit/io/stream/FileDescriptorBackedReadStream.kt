@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import net.rubygrapefruit.io.UnixErrorCode
import platform.posix.EISDIR
import platform.posix.errno
import platform.posix.read

class FileDescriptorBackedReadStream(private val source: StreamSource, private val descriptor: ReadDescriptor) : ReadStream {
    override fun read(buffer: ByteArray, offset: Int, max: Int): ReadResult {
        val nread = read(descriptor.descriptor, buffer.refTo(offset), max.convert()).convert<Int>()
        if (nread < 0) {
            if (errno == EISDIR) {
                return ReadFailed.isNotFile(source)
            } else {
                return ReadFailed.readFile(source, UnixErrorCode.last())
            }
        }
        if (nread == 0) {
            return EndOfStream
        }
        return ReadBytes(nread)
    }
}