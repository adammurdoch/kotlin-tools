@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import net.rubygrapefruit.io.stream.ReadStream
import platform.windows.DWORD
import platform.windows.DWORDVar
import platform.windows.HANDLE
import platform.windows.ReadFile

class FileBackedReadStream(private val path: String, private val handle: HANDLE?) : ReadStream {
    override fun read(buffer: ByteArray, offset: Int, max: Int): Result<Int> {
        return memScoped {
            val nbytes = alloc<DWORDVar>()
            buffer.usePinned { ptr ->
                if (ReadFile(handle, ptr.addressOf(offset), max.convert(), nbytes.ptr, null) == 0) {
                    throw NativeException("Could not read $path.")
                }
            }
            if (nbytes.value == 0.convert<DWORD>()) {
                Success(-1)
            } else {
                Success(nbytes.value.convert())
            }
        }
    }
}