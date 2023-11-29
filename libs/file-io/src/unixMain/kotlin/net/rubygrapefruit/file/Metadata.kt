@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import platform.posix.*

@OptIn(UnsafeNumber::class)
internal fun metadata(path: String): Result<ElementMetadata> {
    return memScoped {
        val statBuf = alloc<stat>()
        if (lstat(path, statBuf.ptr) != 0) {
            if (errno == ENOENT || errno == ENOTDIR) {
                return MissingEntry(path)
            }
            if (errno == EACCES) {
                return UnreadableEntry(path)
            } else {
                throw NativeException("Could not stat file '$path'.")
            }
        }
        val mode = statBuf.st_mode.convert<Int>()
        val lastModified = lastModified(statBuf)
        val permissions = posixPermissions(statBuf)
        if (mode and S_IFDIR == S_IFDIR) {
            Success(DirectoryMetadata(lastModified, permissions))
        } else if (mode and S_IFLNK == S_IFLNK) {
            Success(SymlinkMetadata(lastModified, permissions))
        } else if (mode and S_IFREG == S_IFREG) {
            val size = statBuf.st_size
            Success(RegularFileMetadata(size, lastModified, permissions))
        } else {
            Success(OtherMetadata(lastModified, permissions))
        }
    }
}

internal fun posixPermissions(stat: stat): PosixPermissions {
    return PosixPermissions((mode(stat) and (S_IRWXU or S_IRWXG or S_IRWXO).convert()))
}
