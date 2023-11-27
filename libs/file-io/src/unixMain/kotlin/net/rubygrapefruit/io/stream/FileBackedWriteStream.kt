@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.*
import net.rubygrapefruit.file.FileSystemException
import net.rubygrapefruit.file.UnixErrorCode
import platform.posix.FILE
import platform.posix.fwrite

internal class FileBackedWriteStream(private val path: String, private val file: CPointer<FILE>) : WriteStream {
    override fun write(bytes: ByteArray, offset: Int, count: Int) {
        if (count > 0) {
            memScoped {
                if (fwrite(bytes.refTo(0), 1.convert(), bytes.size.convert(), file) < bytes.size.convert()) {
                    throw FileSystemException(path, UnixErrorCode.last())
                }
            }
        }
    }
}