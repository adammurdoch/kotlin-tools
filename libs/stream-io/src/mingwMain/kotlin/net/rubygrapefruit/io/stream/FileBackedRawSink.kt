@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.UnsafeIoApi
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.WinErrorCode
import net.rubygrapefruit.io.readFrom
import platform.windows.CloseHandle
import platform.windows.DWORDVar
import platform.windows.HANDLE
import platform.windows.WriteFile

@OptIn(UnsafeIoApi::class)
class FileBackedRawSink(private val streamSource: StreamSource, private val handle: HANDLE?) : RawSink {

    override fun write(source: Buffer, byteCount: Long) {
        memScoped {
            val written = alloc<DWORDVar>()
            source.readFrom(byteCount) { buffer, startIndex, count ->
                buffer.usePinned { ptr ->
                    if (WriteFile(handle, ptr.addressOf(startIndex), count.convert(), written.ptr, null) == 0) {
                        throw IOException("Could not write to ${streamSource.displayName}.", WinErrorCode.last())
                    }
                    written.value.convert<Int>()
                }
            }
        }
    }

    override fun flush() {
    }

    override fun close() {
        CloseHandle(handle)
    }
}