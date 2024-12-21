@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import net.rubygrapefruit.error.WinErrorCode
import net.rubygrapefruit.io.writeAtMostTo
import platform.windows.*

@OptIn(UnsafeIoApi::class)
class FileBackedRawSource(private val streamSource: StreamSource, private val handle: HANDLE) : RawSource {
    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        return memScoped {
            val nbytes = alloc<DWORDVar>()
            val ncopied = sink.writeAtMostTo(byteCount) { buffer, startIndex, count ->
                buffer.usePinned { ptr ->
                    if (ReadFile(handle, ptr.addressOf(startIndex), count.convert(), nbytes.ptr, null) == 0) {
                        if (GetLastError().convert<Int>() != ERROR_BROKEN_PIPE) {
                            throw streamSource.readFailed(WinErrorCode.last())
                        }
                        nbytes.value = 0.convert()
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