@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.io.stream

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.io.unsafe.withData
import net.rubygrapefruit.file.NativeException
import platform.windows.CloseHandle
import platform.windows.DWORDVar
import platform.windows.HANDLE
import platform.windows.WriteFile

@OptIn(UnsafeIoApi::class)
internal class FileBackedRawSink(private val path: String, private val handle: HANDLE?) : RawSink {

    override fun write(source: Buffer, byteCount: Long) {
        memScoped {
            val nbytes = alloc<DWORDVar>()
            UnsafeBufferOperations.forEachSegment(source) { context, segment ->
                context.withData(segment) { buffer, startIndex, endIndex ->
                    buffer.usePinned { ptr ->
                        var pos = startIndex
                        var remaining = endIndex - startIndex
                        while (remaining > 0) {
                            if (WriteFile(handle, ptr.addressOf(pos), remaining.convert(), nbytes.ptr, null) == 0) {
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
    }

    override fun flush() {
    }

    override fun close() {
        CloseHandle(handle)
    }
}