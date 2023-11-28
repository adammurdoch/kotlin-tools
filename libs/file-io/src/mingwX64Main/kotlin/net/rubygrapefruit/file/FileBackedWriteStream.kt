@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import net.rubygrapefruit.io.stream.WriteStream
import platform.windows.DWORDVar
import platform.windows.HANDLE
import platform.windows.WriteFile

class FileBackedWriteStream(private val path: String, private val handle: HANDLE?) : WriteStream {
    override fun write(bytes: ByteArray, offset: Int, count: Int) {
        memScoped {
            var pos = 0
            val nbytes = alloc<DWORDVar>()
            while (pos < bytes.size) {
                bytes.usePinned { ptr ->
                    if (WriteFile(handle, ptr.addressOf(pos), (bytes.size - pos).convert(), nbytes.ptr, null) == 0) {
                        throw NativeException("Could not write to file $path.")
                    }
                    pos += nbytes.value.convert<Int>()
                }
            }
        }
    }
}