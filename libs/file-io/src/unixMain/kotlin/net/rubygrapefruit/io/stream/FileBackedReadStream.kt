@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import net.rubygrapefruit.file.UnixErrorCode
import platform.posix.EISDIR
import platform.posix.errno
import platform.posix.read

internal class FileBackedReadStream(private val path: String, private val des: Int) : ReadStream {
    override fun read(buffer: ByteArray, offset: Int, max: Int): ReadResult {
        val nread = read(des, buffer.refTo(offset), max.convert()).convert<Int>()
        if (nread < 0) {
            if (errno == EISDIR) {
                return ReadFailed.isNotFile(path)
            } else {
                return ReadFailed.readFile(path, UnixErrorCode.last())
            }
        }
        if (nread == 0) {
            return EndOfStream
        }
        return ReadBytes(nread)
    }
}