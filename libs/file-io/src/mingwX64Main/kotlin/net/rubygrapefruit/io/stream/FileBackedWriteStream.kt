@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.*
import net.rubygrapefruit.file.NativeException
import platform.windows.DWORDVar
import platform.windows.HANDLE
import platform.windows.WriteFile

internal class FileBackedWriteStream(private val path: String, private val handle: HANDLE?) : WriteStream {
    override fun write(bytes: ByteArray, offset: Int, count: Int) {
        memScoped {
            var pos = offset
            var remaining = count
            val nbytes = alloc<DWORDVar>()
            while (remaining > 0) {
                bytes.usePinned { ptr ->
                    if (WriteFile(handle, ptr.addressOf(pos), (remaining).convert(), nbytes.ptr, null) == 0) {
                        throw NativeException("Could not write to file $path.")
                    }
                }
                val bytesWritten = nbytes.value.convert<Int>()
                pos += bytesWritten
                remaining -= bytesWritten
            }
        }
    }
}