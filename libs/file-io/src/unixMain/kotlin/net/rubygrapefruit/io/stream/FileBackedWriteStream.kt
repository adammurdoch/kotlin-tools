@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.*
import net.rubygrapefruit.file.FileSystemException
import net.rubygrapefruit.file.UnixErrorCode
import net.rubygrapefruit.io.IOException
import platform.posix.FILE
import platform.posix.fwrite

internal class FileBackedWriteStream(private val path: String, private val file: CPointer<FILE>) : WriteStream {
    override fun write(bytes: ByteArray, offset: Int, count: Int) {
        if (count > 0) {
            memScoped {
                if (fwrite(bytes.refTo(offset), 1.convert(), count.convert(), file) < count.convert()) {
                    throw IOException("Could not write to file $path.", UnixErrorCode.last())
                }
            }
        }
    }
}