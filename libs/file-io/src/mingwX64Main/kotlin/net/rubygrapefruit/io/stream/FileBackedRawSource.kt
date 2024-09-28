@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import net.rubygrapefruit.file.NativeException
import net.rubygrapefruit.io.writeAtMostTo
import platform.windows.CloseHandle
import platform.windows.DWORDVar
import platform.windows.HANDLE
import platform.windows.ReadFile

@OptIn(UnsafeIoApi::class)
internal class FileBackedRawSource(private val path: String, private val handle: HANDLE?) : RawSource {
    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        return memScoped {
            val nbytes = alloc<DWORDVar>()
            val ncopied = sink.writeAtMostTo(byteCount) { buffer, startIndex, count ->
                buffer.usePinned { ptr ->
                    if (ReadFile(handle, ptr.addressOf(startIndex), count.convert(), nbytes.ptr, null) == 0) {
                        throw NativeException("Could not read $path.")
                    }
                }
                nbytes.value.convert()
            }
            return if (ncopied == 0) -1 else ncopied.toLong()
        }
    }

    override fun close() {
        CloseHandle(handle)
    }
}