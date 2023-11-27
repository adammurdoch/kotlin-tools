@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import platform.posix.free
import platform.windows.*

private class WinFileSystem : FileSystem {
    override val currentDirectory: Directory = getCurrentDirectory()

    override val userHomeDirectory: Directory = getHomeDirectory()
}

actual val fileSystem: FileSystem = WinFileSystem()

private fun getCurrentDirectory(): WinDirectory {
    return memScoped {
        val size = GetCurrentDirectoryW(0.convert(), null).convert<Int>()
        if (size <= 0) {
            throw NativeException("Could not get current directory.")
        }
        val buffer = allocArray<WCHARVar>(size)
        val chars = GetCurrentDirectoryW(size.convert(), buffer).convert<Int>()
        if (chars <= 0) {
            throw NativeException("Could not get current directory.")
        }
        WinDirectory(WinPath(buffer.toKString()))
    }
}

private fun getHomeDirectory(): WinDirectory {
    return memScoped {
        val buffer = allocArray<WCHARVar>(MAX_PATH)
        if (SHGetFolderPathW(null, CSIDL_PROFILE, null, SHGFP_TYPE_CURRENT, buffer) != S_OK) {
            throw FileSystemException("Could not get user home directory.")
        }
        WinDirectory(WinPath(buffer.toKString()))
    }
}