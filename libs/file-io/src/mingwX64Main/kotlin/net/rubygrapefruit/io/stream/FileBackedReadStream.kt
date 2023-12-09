@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.*
import net.rubygrapefruit.file.NativeException
import platform.windows.DWORD
import platform.windows.DWORDVar
import platform.windows.HANDLE
import platform.windows.ReadFile

internal class FileBackedReadStream(private val path: String, private val handle: HANDLE?) : ReadStream {
    override fun read(buffer: ByteArray, offset: Int, max: Int): ReadResult {
        return memScoped {
            val nbytes = alloc<DWORDVar>()
            buffer.usePinned { ptr ->
                if (ReadFile(handle, ptr.addressOf(offset), max.convert(), nbytes.ptr, null) == 0) {
                    throw NativeException("Could not read $path.")
                }
            }
            if (nbytes.value == 0.convert<DWORD>()) {
                EndOfStream
            } else {
                ReadBytes(nbytes.value.convert())
            }
        }
    }
}