@file:OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import net.rubygrapefruit.io.Resource
import net.rubygrapefruit.io.WinErrorCode
import net.rubygrapefruit.io.stream.FileBackedRawSink
import net.rubygrapefruit.io.stream.FileBackedRawSource
import platform.windows.*

internal open class WinFileSystemElement(override val path: ElementPath) : AbstractFileSystemElement() {

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

    override fun posixPermissions(): PosixPermissions {
        throw readPermissionNotSupported(path.absolutePath)
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

internal class WinElementSnapshot(override val path: ElementPath, override val metadata: ElementMetadata) : AbstractElementSnapshot() {
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

internal class WinDirectory(path: ElementPath) : WinFileSystemElement(path), Directory {

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
                    ERROR_ALREADY_EXISTS -> {
                        if (metadata().directory) {
                            return
                        }
                        throw createDirectoryThatExistsAndIsNotADir(absolutePath)
                    }

                    ERROR_PATH_NOT_FOUND -> {
                        // Continue below
                    }

                    else -> throw createDirectory(this@WinDirectory, errorCode = WinErrorCode.last())
                }
            }
        }
        parent?.createDirectories()
        memScoped {
            if (CreateDirectoryW(absolutePath, null) == 0) {
                when (GetLastError().convert<Int>()) {
                    ERROR_ALREADY_EXISTS -> return
                    else -> throw createDirectory(this@WinDirectory, errorCode = WinErrorCode.last())
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

    override fun listEntries(): List<DirectoryEntry> {
        return memScoped {
            val data = alloc<WIN32_FIND_DATAW>()
            val handle = FindFirstFileW("$absolutePath\\*", data.ptr)
            if (handle == INVALID_HANDLE_VALUE) {
                throw when (GetLastError().convert<Int>()) {
                    ERROR_PATH_NOT_FOUND ->
                        listDirectoryThatDoesNotExist(absolutePath)

                    ERROR_DIRECTORY ->
                        listDirectoryThatIsNotADirectory(absolutePath)

                    else ->
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
                        throw listDirectory(this@WinDirectory, errorCode = WinErrorCode.last())
                    }
                }
                result
            } finally {
                FindClose(handle)
            }
        }
    }

    override fun visitTopDown(visitor: DirectoryEntry.() -> Unit) {
        visitTopDown(this, visitor)
    }
}

private class WinDirectoryEntry(private val parentPath: ElementPath, override val name: String, override val type: ElementType) : DirectoryEntry {
    override val path: ElementPath
        get() = parentPath.resolve(name)

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata(path.absolutePath).map { WinElementSnapshot(path, it) }
    }

    override fun toDir(): Directory {
        return WinDirectory(path)
    }

    override fun toFile(): RegularFile {
        return WinRegularFile(path)
    }

    override fun toSymLink(): SymLink {
        return WinSymLink(path)
    }
}

internal class WinRegularFile(path: ElementPath) : WinFileSystemElement(path), RegularFile {
    override val parent: WinDirectory
        get() = super.parent!!

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

    override fun <T> withContent(action: (FileContent) -> T): T {
        return doOpenContent().use { content ->
            action(content)
        }
    }

    override fun openContent(): Resource<FileContent> {
        return Resource.of(doOpenContent())
    }

    private fun doOpenContent(): WinFileContent {
        val handle = CreateFileW(
            absolutePath,
            (GENERIC_WRITE.toUInt() or GENERIC_READ).convert<DWORD>(),
            0.convert<DWORD>(),
            null,
            OPEN_ALWAYS.convert<DWORD>(),
            FILE_ATTRIBUTE_NORMAL.convert<DWORD>(),
            null
        )
        if (handle == INVALID_HANDLE_VALUE) {
            throw openFile(this@WinRegularFile, errorCode = WinErrorCode.last())
        }
        return WinFileContent(absolutePath, handle)
    }

    override fun <T> write(action: (Sink) -> T): T {
        val handle = CreateFileW(
            absolutePath,
            GENERIC_WRITE.convert<DWORD>(),
            0.convert<DWORD>(),
            null,
            CREATE_ALWAYS.convert<DWORD>(),
            FILE_ATTRIBUTE_NORMAL.convert<DWORD>(),
            null
        )
        if (handle == INVALID_HANDLE_VALUE) {
            throw writeToFile(this@WinRegularFile, errorCode = WinErrorCode.last())
        }
        return try {
            FileBackedRawSink(absolutePath, handle).use {
                val buffered = it.buffered()
                val result = action(buffered)
                buffered.flush()
                result
            }
        } finally {
            CloseHandle(handle)
        }
    }

    override fun <T> read(action: (Source) -> T): T {
        val handle = CreateFileW(
            absolutePath,
            GENERIC_READ.convert<DWORD>(),
            0.convert<DWORD>(),
            null,
            OPEN_EXISTING.convert<DWORD>(),
            FILE_ATTRIBUTE_NORMAL.convert<DWORD>(),
            null
        )
        if (handle == INVALID_HANDLE_VALUE) {
            if (GetLastError().convert<Int>() == ERROR_FILE_NOT_FOUND) {
                throw readFileThatDoesNotExist(absolutePath)
            }
            throw readFile(this@WinRegularFile, errorCode = WinErrorCode.last())
        }
        return try {
            FileBackedRawSource(absolutePath, handle).use {
                val buffered = it.buffered()
                action(buffered)
            }
        } finally {
            CloseHandle(handle)
        }
    }
}

internal class WinSymLink(path: ElementPath) : WinFileSystemElement(path), SymLink {
    override val parent: WinDirectory
        get() = super.parent!!

    override fun toSymLink(): SymLink {
        return this
    }

    override fun readSymLink(): String {
        return memScoped {
            val handle = CreateFileW(absolutePath, GENERIC_READ, 0.convert(), null, OPEN_EXISTING.convert(), FILE_ATTRIBUTE_NORMAL.convert(), null)
            if (handle == INVALID_HANDLE_VALUE) {
                if (GetLastError().convert<Int>() == ERROR_PATH_NOT_FOUND) {
                    throw readMissingSymlink(absolutePath)
                }
                throw NativeException("Could not read symlink $absolutePath")
            }
            try {
                val size = GetFinalPathNameByHandleW(handle, null, 0.convert(), FILE_NAME_NORMALIZED.convert())
                val buffer = allocArray<WCHARVar>(size.convert())
                GetFinalPathNameByHandleW(handle, buffer, size, FILE_NAME_NORMALIZED.convert())
                buffer.toKString().removePrefix("""\\?""")
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