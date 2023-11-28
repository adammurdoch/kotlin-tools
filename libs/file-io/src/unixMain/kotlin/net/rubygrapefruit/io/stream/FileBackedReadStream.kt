@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import net.rubygrapefruit.file.NativeException
import net.rubygrapefruit.file.Result
import net.rubygrapefruit.file.Success
import net.rubygrapefruit.file.readFileThatIsNotAFile
import platform.posix.EISDIR
import platform.posix.errno
import platform.posix.read

internal class FileBackedReadStream(private val path: String, private val des: Int) : ReadStream {
    override fun read(buffer: ByteArray, offset: Int, max: Int): Result<Int> {
        val nread = read(des, buffer.refTo(offset), max.convert()).convert<Int>()
        if (nread < 0) {
            if (errno == EISDIR) {
                return readFileThatIsNotAFile(path)
            } else {
                throw NativeException("Could not read from $path.")
            }
        }
        if (nread == 0) {
            return Success(-1)
        }
        return Success(nread)
    }
}