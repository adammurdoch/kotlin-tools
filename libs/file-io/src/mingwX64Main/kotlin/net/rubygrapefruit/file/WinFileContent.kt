@file:OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import net.rubygrapefruit.io.stream.FileBackedRawSink
import net.rubygrapefruit.io.stream.FileBackedRawSource
import net.rubygrapefruit.io.stream.FileBackedReadStream
import net.rubygrapefruit.io.stream.FileBackedWriteStream
import net.rubygrapefruit.io.stream.ReadStream
import net.rubygrapefruit.io.stream.WriteStream
import platform.windows.*

class WinFileContent(
    path: String,
    private val handle: HANDLE?
) : FileContent, AutoCloseable {
    override val currentPosition: Long
        get() {
            return memScoped {
                val high = alloc<LONGVar>()
                high.value = 0
                val result = SetFilePointer(handle, 0, high.ptr, FILE_CURRENT.convert())
                if (result == INVALID_SET_FILE_POINTER) {
                    throw NativeException("Could not get current position")
                }
                high.value.long().shl(32).or(result.long())
            }
        }

    override val writeStream: WriteStream = FileBackedWriteStream(path, handle)

    override val readStream: ReadStream = FileBackedReadStream(path, handle)

    override val sink: RawSink = FileBackedRawSink(path, handle)

    override val source: RawSource = FileBackedRawSource(path, handle)

    override fun length(): Long {
        return memScoped {
            val size = alloc<LARGE_INTEGER>()
            if (GetFileSizeEx(handle, size.ptr) == 0) {
                throw NativeException("Could not get file size")
            }
            size.HighPart.long().shl(32).or(size.LowPart.long())
        }
    }

    override fun seek(position: Long) {
        memScoped {
            val high = alloc<LONGVar>()
            high.value = position.ushr(32).toInt()
            val result = SetFilePointer(handle, position.toInt(), high.ptr, FILE_BEGIN.convert())
            if (result == INVALID_SET_FILE_POINTER) {
                throw NativeException("Could not set current position")
            }
        }
    }

    override fun seekToEnd(): Long {
        return memScoped {
            val high = alloc<LONGVar>()
            high.value = 0
            val result = SetFilePointer(handle, 0, high.ptr, FILE_END.convert())
            if (result == INVALID_SET_FILE_POINTER) {
                throw NativeException("Could not set current position")
            }
            high.value.long().shl(32).or(result.long())
        }
    }

    override fun close() {
        CloseHandle(handle)
    }

    private fun LONG.long() = toLong().and(0xFFFF)

    private fun DWORD.long() = toLong().and(0xFFFF)
}