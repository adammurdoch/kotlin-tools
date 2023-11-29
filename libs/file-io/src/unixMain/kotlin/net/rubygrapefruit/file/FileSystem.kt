@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import platform.posix.MAXPATHLEN
import platform.posix.getcwd
import platform.posix.getpwuid
import platform.posix.getuid

private class NativeFileSystem : FileSystem {
    override val currentDirectory: Directory
        get() = getCurrentDir()

    override val userHomeDirectory: Directory
        get() = getUserHomeDir()
}

actual val fileSystem: FileSystem = NativeFileSystem()

internal fun getUserHomeDir(): UnixDirectory {
    return memScoped {
        val uid = getuid()
        val pwd = getpwuid(uid)
        if (pwd == null) {
            throw NativeException("Could not get user home directory.")
        }
        UnixDirectory(AbsolutePath(pwd.pointed.pw_dir!!.toKString()))
    }
}

internal fun getCurrentDir(): UnixDirectory {
    return memScoped {
        val length = MAXPATHLEN
        val buffer = allocArray<ByteVar>(length)
        val path = getcwd(buffer, length.convert())
        if (path == null) {
            throw NativeException("Could not get current directory.")
        }
        UnixDirectory(AbsolutePath(buffer.toKString()))
    }
}
