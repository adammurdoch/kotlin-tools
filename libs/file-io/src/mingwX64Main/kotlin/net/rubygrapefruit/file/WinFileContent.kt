@file:OptIn(ExperimentalForeignApi::class)

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
) : FileContent {
    override val currentPosition: UInt
        get() {
            val result = SetFilePointer(handle, 0, null, FILE_CURRENT.convert())
            if (result == INVALID_SET_FILE_POINTER) {
                throw NativeException("Could not get current position")
            }
            return result
        }

    override val writeStream: WriteStream = FileBackedWriteStream(path, handle)

    override val readStream: ReadStream = FileBackedReadStream(path, handle)

    override fun length(): UInt {
        return memScoped {
            val size = alloc<LARGE_INTEGER>()
            if (GetFileSizeEx(handle, size.ptr) == 0) {
                throw NativeException("Could not get file size")
            }
            size.LowPart
        }
    }

    override fun seek(position: UInt) {
        val result = SetFilePointer(handle, position.convert(), null, FILE_BEGIN.convert())
        if (result == INVALID_SET_FILE_POINTER) {
            throw NativeException("Could not set current position")
        }
    }

    override fun seekToEnd() {
        val result = SetFilePointer(handle, 0, null, FILE_END.convert())
        if (result == INVALID_SET_FILE_POINTER) {
            throw NativeException("Could not set current position")
        }
    }
}