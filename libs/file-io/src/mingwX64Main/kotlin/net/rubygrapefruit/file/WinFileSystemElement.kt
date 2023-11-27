@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import platform.windows.*

internal open class WinFileSystemElement(override val path: WinPath) : AbstractFileSystemElement() {

    override val parent: WinDirectory?
        get() {
            val parentPath = path.parent
            return if (parentPath == null) {
                null
            } else {
                WinDirectory(parentPath)
            }
        }

    override fun metadata(): Result<ElementMetadata> {
        return memScoped {
            val data = alloc<WIN32_FILE_ATTRIBUTE_DATA>()
            if (GetFileAttributesExW(absolutePath, _GET_FILEEX_INFO_LEVELS.GetFileExInfoStandard, data.ptr) == 0) {
                when (GetLastError().convert<Int>()) {
                    ERROR_FILE_NOT_FOUND, ERROR_PATH_NOT_FOUND -> return MissingEntry(absolutePath, null)
                    else -> throw NativeException("Could not query metadata of $absolutePath.")
                }
            }
            val dirMask = FILE_ATTRIBUTE_DIRECTORY.convert<DWORD>()
            val centaNanos = toLong(data.ftLastWriteTime.dwHighDateTime, data.ftLastWriteTime.dwLowDateTime)
            val timestamp = Timestamp(centaNanos * 100)
            val metadata = when {
                data.dwFileAttributes and dirMask == dirMask -> DirectoryMetadata(timestamp, null)
                else -> RegularFileMetadata(toLong(data.nFileSizeHigh, data.nFileSizeLow), timestamp, null)
            }
            Success(metadata)
        }
    }

    private inline fun toLong(high: DWORD, low: DWORD): Long = (high.convert<Long>() shl 32) or (low.convert())

    override fun posixPermissions(): Result<PosixPermissions> {
        TODO("Not yet implemented")
    }

    override fun setPermissions(permissions: PosixPermissions) {
        TODO("Not yet implemented")
    }

    override fun supports(capability: FileSystemCapability): Boolean {
        return when (capability) {
            FileSystemCapability.PosixPermissions -> false
            FileSystemCapability.SetSymLinkPosixPermissions -> false
        }
    }

    override fun snapshot(): Result<ElementSnapshot> {
        TODO("Not yet implemented")
    }

    override fun toFile(): RegularFile {
        return WinRegularFile(path)
    }

    override fun toDir(): Directory {
        return WinDirectory(path)
    }

    override fun toSymLink(): SymLink {
        return WinSymLink(path)
    }
}

internal class WinDirectory(path: WinPath) : WinFileSystemElement(path), Directory {

    override fun toDir(): Directory {
        return this
    }

    override fun file(name: String): RegularFile {
        return WinRegularFile(path.resolve(name))
    }

    override fun dir(name: String): Directory {
        return WinDirectory(path.resolve(name))
    }

    override fun symLink(name: String): SymLink {
        return WinSymLink(path.resolve(name))
    }

    override fun deleteRecursively() {
        TODO("Not yet implemented")
    }

    override fun createDirectories() {
        memScoped {
            if (CreateDirectoryW(absolutePath, null) == 0) {
                when (GetLastError().convert<Int>()) {
                    ERROR_ALREADY_EXISTS -> return
                    ERROR_PATH_NOT_FOUND -> {
                        // Continue below
                    }

                    else -> throw NativeException("Could not create directory $absolutePath.")
                }
            }
        }
        parent?.createDirectories()
        memScoped {
            if (CreateDirectoryW(absolutePath, null) == 0) {
                when (GetLastError().convert<Int>()) {
                    ERROR_ALREADY_EXISTS -> return
                    else -> throw NativeException("Could not create directory $absolutePath.")
                }
            }
        }
    }

    override fun createTemporaryDirectory(): Directory {
        return memScoped {
            val buffer = allocArray<WCHARVar>(MAX_PATH)
            if (GetTempFileNameW(absolutePath, "tmp", 0.convert(), buffer) == 0.convert<UINT>()) {
                throw NativeException("Could not create temporary directory in $absolutePath.")
            }
            // This is not atomic, should fail
            val dirName = buffer.toKString()
            if (DeleteFileW(dirName) == 0) {
                throw NativeException("Could not delete temporary file $dirName.")
            }
            if (CreateDirectoryW(dirName, null) == 0) {
                throw NativeException("Could not create temporary directory $dirName.")
            }
            WinDirectory(WinPath(dirName))
        }
    }

    override fun listEntries(): Result<List<DirectoryEntry>> {
        return memScoped {
            val data = alloc<WIN32_FIND_DATAW>()
            val handle = FindFirstFileW("$absolutePath\\*", data.ptr)
            if (handle == INVALID_HANDLE_VALUE) {
                throw NativeException("Could not list entries for directory $absolutePath.")
            }
            try {
                val result = mutableListOf<DirectoryEntry>()
                while (true) {
                    val name = data.cFileName.toKString()
                    if (name != "." && name != "..") {
                        result.add(WinDirectoryEntry(path, name, ElementType.Other))
                    }
                    if (FindNextFileW(handle, data.ptr) == 0) {
                        if (GetLastError().convert<Int>() == ERROR_NO_MORE_FILES) {
                            break
                        }
                        throw NativeException("Could not list entries for directory $absolutePath.")
                    }
                }
                Success(result)
            } finally {
                FindClose(handle)
            }
        }
    }

    override fun visitTopDown(visitor: DirectoryEntry.() -> Unit) {
        TODO("Not yet implemented")
    }
}

private class WinDirectoryEntry(private val parentPath: WinPath, override val name: String, override val type: ElementType) : DirectoryEntry {
    override val path: WinPath
        get() = parentPath.resolve(name)

    override fun snapshot(): Result<ElementSnapshot> {
        TODO()
    }

    override fun toDir(): Directory {
        TODO()
    }

    override fun toFile(): RegularFile {
        TODO()
    }

    override fun toSymLink(): SymLink {
        TODO()
    }
}

internal class WinRegularFile(path: WinPath) : WinFileSystemElement(path), RegularFile {

    override fun toFile(): RegularFile {
        return this
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override fun writeBytes(bytes: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun readBytes(): Result<ByteArray> {
        TODO("Not yet implemented")
    }

    override fun writeText(text: String) {
        TODO("Not yet implemented")
    }

    override fun readText(): Result<String> {
        TODO("Not yet implemented")
    }
}

internal class WinSymLink(path: WinPath) : WinFileSystemElement(path), SymLink {

    override fun toSymLink(): SymLink {
        return this
    }

    override fun readSymLink(): Result<String> {
        TODO("Not yet implemented")
    }

    override fun writeSymLink(target: String) {
        TODO("Not yet implemented")
    }
}