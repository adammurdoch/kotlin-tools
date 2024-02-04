@file:OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
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
        val result = SetFilePointer(handle, 0, null, FILE_END.convert())
        if (result == INVALID_SET_FILE_POINTER) {
            throw NativeException("Could not set current position")
        }
        // Need to pass in parameter to get the high 4 bytes
        TODO()
    }

    override fun close() {
        CloseHandle(handle)
    }

    private fun LONG.long() = toLong().and(0xFFFF)

    private fun DWORD.long() = toLong().and(0xFFFF)
}