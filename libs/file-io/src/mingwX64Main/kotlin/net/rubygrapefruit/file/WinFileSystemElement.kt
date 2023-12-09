@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import net.rubygrapefruit.io.TryFailure
import net.rubygrapefruit.io.stream.CollectingBuffer
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
        return metadata(absolutePath)
    }

    override fun posixPermissions(): Result<PosixPermissions> {
        return readPermissionNotSupported(path.absolutePath)
    }

    override fun setPermissions(permissions: PosixPermissions) {
        throw setPermissionsNotSupported(path.absolutePath)
    }

    override fun supports(capability: FileSystemCapability): Boolean {
        return when (capability) {
            FileSystemCapability.PosixPermissions -> false
            FileSystemCapability.SetSymLinkPosixPermissions -> false
        }
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata().map { WinElementSnapshot(path, it) }
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

internal class WinElementSnapshot(override val path: WinPath, override val metadata: ElementMetadata) : AbstractElementSnapshot() {
    override fun asDirectory(): Directory {
        return WinDirectory(path)
    }

    override fun asRegularFile(): RegularFile {
        return WinRegularFile(path)
    }

    override fun asSymLink(): SymLink {
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
        deleteRecursively(this) { entry ->
            if (entry.type == ElementType.Directory) {
                if (RemoveDirectoryW(entry.absolutePath) == 0) {
                    throw NativeException("Could not delete directory ${entry.absolutePath}")
                }
            } else {
                if (DeleteFileW(entry.absolutePath) == 0) {
                    throw NativeException("Could not delete file ${entry.absolutePath}")
                }
            }
        }
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
                return if (GetLastError().convert<Int>() == ERROR_FILE_NOT_FOUND) {
                    listDirectoryThatDoesNotExist(absolutePath)
                } else {
                    listDirectory(this@WinDirectory, errorCode = WinErrorCode.last())
                }
            }
            try {
                val result = mutableListOf<DirectoryEntry>()
                while (true) {
                    val name = data.cFileName.toKString()
                    if (name != "." && name != "..") {
                        val type = when {
                            data.dwFileAttributes and dirMask == dirMask -> ElementType.Directory
                            data.dwFileAttributes and symLinkMask == symLinkMask -> ElementType.SymLink
                            else -> ElementType.RegularFile
                        }
                        result.add(WinDirectoryEntry(path, name, type))
                    }
                    if (FindNextFileW(handle, data.ptr) == 0) {
                        if (GetLastError().convert<Int>() == ERROR_NO_MORE_FILES) {
                            break
                        }
                        return listDirectory(this@WinDirectory, errorCode = WinErrorCode.last())
                    }
                }
                Success(result)
            } finally {
                FindClose(handle)
            }
        }
    }

    override fun visitTopDown(visitor: DirectoryEntry.() -> Unit) {
        visitTopDown(this, visitor)
    }
}

private class WinDirectoryEntry(private val parentPath: WinPath, override val name: String, override val type: ElementType) : DirectoryEntry {
    override val path: WinPath
        get() = parentPath.resolve(name)

    override fun snapshot(): Result<ElementSnapshot> {
        TODO()
    }

    override fun toDir(): Directory {
        return WinDirectory(path)
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
        delete(this) { file ->
            if (DeleteFileW(file.absolutePath) == 0) {
                throw deleteFile(absolutePath, WinErrorCode.last())
            }
        }
    }

    override fun writeBytes(bytes: ByteArray) {
        memScoped {
            val handle = CreateFileW(absolutePath, GENERIC_WRITE.convert(), 0.convert(), null, CREATE_ALWAYS.convert(), FILE_ATTRIBUTE_NORMAL.convert(), null)
            if (handle == INVALID_HANDLE_VALUE) {
                throw writeToFile(this@WinRegularFile, WinErrorCode.last())
            }
            try {
                FileBackedWriteStream(absolutePath, handle).write(bytes)
            } finally {
                CloseHandle(handle)
            }
        }
    }

    override fun readBytes(): Result<ByteArray> {
        memScoped {
            val handle = CreateFileW(absolutePath, GENERIC_READ.convert(), 0.convert(), null, OPEN_EXISTING.convert(), FILE_ATTRIBUTE_NORMAL.convert(), null)
            if (handle == INVALID_HANDLE_VALUE) {
                return if (GetLastError().convert<Int>() == ERROR_FILE_NOT_FOUND) {
                    readFileThatDoesNotExist(absolutePath)
                } else {
                    readFile(this@WinRegularFile, WinErrorCode.last())
                }
            }
            try {
                val buffer = CollectingBuffer()
                val result = buffer.readFrom(FileBackedReadStream(absolutePath, handle))
                return if (result is TryFailure) {
                    FailedOperation(result.exception)
                } else {
                    Success(buffer.toByteArray())
                }
            } finally {
                CloseHandle(handle)
            }
        }
    }

    override fun writeText(text: String) {
        writeBytes(text.encodeToByteArray())
    }

    override fun readText(): Result<String> {
        return readBytes().map { it.decodeToString() }
    }
}

internal class WinSymLink(path: WinPath) : WinFileSystemElement(path), SymLink {

    override fun toSymLink(): SymLink {
        return this
    }

    override fun readSymLink(): Result<String> {
        return memScoped {
            val handle = CreateFileW(absolutePath, GENERIC_READ, 0.convert(), null, OPEN_EXISTING.convert(), FILE_ATTRIBUTE_NORMAL.convert(), null)
            try {
                val size = GetFinalPathNameByHandleW(handle, null, 0.convert(), FILE_NAME_NORMALIZED.convert())
                val buffer = allocArray<WCHARVar>(size.convert())
                GetFinalPathNameByHandleW(handle, buffer, size, FILE_NAME_NORMALIZED.convert())
                Success(buffer.toKString())
            } finally {
                CloseHandle(handle)
            }
        }
    }

    override fun writeSymLink(target: String) {
        if (CreateSymbolicLinkW(absolutePath, target, 0.convert()) == 0.convert<BOOLEAN>()) {
            throw NativeException("Could not create symbolic link $absolutePath.")
        }
    }
}