@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import platform.windows.*

internal val dirMask = FILE_ATTRIBUTE_DIRECTORY.convert<DWORD>()
internal val symLinkMask = FILE_ATTRIBUTE_REPARSE_POINT.convert<DWORD>()

internal fun metadata(absolutePath: String): Result<ElementMetadata> {
    return memScoped {
        val data = alloc<WIN32_FILE_ATTRIBUTE_DATA>()
        if (GetFileAttributesExW(absolutePath, _GET_FILEEX_INFO_LEVELS.GetFileExInfoStandard, data.ptr) == 0) {
            when (GetLastError().convert<Int>()) {
                ERROR_FILE_NOT_FOUND, ERROR_PATH_NOT_FOUND -> return MissingEntry(absolutePath, null)
                else -> throw NativeException("Could not query metadata of $absolutePath.")
            }
        }
        val centaNanos = toLong(data.ftLastWriteTime.dwHighDateTime, data.ftLastWriteTime.dwLowDateTime)
        val timestamp = Timestamp(centaNanos * 100)
        val metadata = when {
            data.dwFileAttributes and dirMask == dirMask -> DirectoryMetadata(timestamp, null)
            data.dwFileAttributes and symLinkMask == symLinkMask -> SymlinkMetadata(timestamp, null)
            else -> RegularFileMetadata(toLong(data.nFileSizeHigh, data.nFileSizeLow), timestamp, null)
        }
        Success(metadata)
    }
}

private inline fun toLong(high: DWORD, low: DWORD): Long = (high.convert<Long>() shl 32) or (low.convert())
