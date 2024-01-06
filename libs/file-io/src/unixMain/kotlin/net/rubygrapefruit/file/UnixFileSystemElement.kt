@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import net.rubygrapefruit.io.UnixErrorCode
import net.rubygrapefruit.io.stream.*
import platform.posix.*

internal open class UnixFileSystemElement(override val path: AbsolutePath) : AbstractFileSystemElement() {
    override val parent: Directory?
        get() {
            val parentPath = path.parent
            return if (parentPath == null) {
                null
            } else {
                UnixDirectory(parentPath)
            }
        }

    override fun metadata(): Result<ElementMetadata> {
        return metadata(path.absolutePath)
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata().map { UnixSnapshot(path, it) }
    }

    override fun toDir(): Directory {
        return UnixDirectory(path)
    }

    override fun toFile(): RegularFile {
        return UnixRegularFile(path)
    }

    override fun toSymLink(): SymLink {
        return UnixSymLink(path)
    }

    @OptIn(UnsafeNumber::class)
    override fun posixPermissions(): Result<PosixPermissions> {
        return memScoped {
            val statBuf = alloc<stat>()
            if (lstat(path.absolutePath, statBuf.ptr) != 0) {
                if (errno == ENOENT) {
                    readPermissionOnMissingElement(absolutePath)
                } else {
                    readPermission(absolutePath, errorCode = UnixErrorCode.last())
                }
            } else {
                Success(posixPermissions(statBuf))
            }
        }
    }

    @OptIn(UnsafeNumber::class)
    override fun setPermissions(permissions: PosixPermissions) {
        if (lchmod(path.absolutePath, permissions.mode.convert()) != 0) {
            if (errno == ENOTSUP) {
                throw setPermissionsNotSupported(absolutePath)
            } else if (errno == ENOENT) {
                throw setPermissionsOnMissingElement(absolutePath)
            } else {
                throw setPermissions(absolutePath, errorCode = UnixErrorCode.last())
            }
        }
    }

    override fun supports(capability: FileSystemCapability): Boolean {
        return when (capability) {
            FileSystemCapability.SetSymLinkPosixPermissions -> canSetSymLinkPermissions
            FileSystemCapability.PosixPermissions -> true
        }
    }

    protected fun MemScope.fileSize(): Long {
        val statBuf = alloc<stat>()
        if (lstat(path.absolutePath, statBuf.ptr) != 0) {
            throw NativeException("Could not stat $path.")
        }
        return statBuf.st_size
    }
}

internal class UnixRegularFile(path: AbsolutePath) : UnixFileSystemElement(path), RegularFile {

    override fun toFile(): RegularFile {
        return this
    }

    override fun delete() {
        delete(this) {
            if (remove(it.path.absolutePath) != 0) {
                throw deleteFile(it.path.absolutePath, UnixErrorCode.last())
            }
        }
    }

    override fun <T> withContent(action: (FileContent) -> T): Result<T> {
        return memScoped {
            val des = doOpen(path.absolutePath, O_RDWR or O_CREAT or O_NOFOLLOW or O_CLOEXEC, PosixPermissions.readWriteFile.mode)
            if (des < 0) {
                throw NativeException("Could not open $absolutePath")
            }
            try {
                Success(action(UnixFileContent(FileSource(path), des)))
            } finally {
                close(des)
            }
        }
    }

    override fun writeBytes(action: (WriteStream) -> Unit) {
        memScoped {
            val des = doOpen(path.absolutePath, O_WRONLY or O_CREAT or O_TRUNC or O_NOFOLLOW or O_CLOEXEC, PosixPermissions.readWriteFile.mode)
            if (des < 0) {
                if (errno == EISDIR) {
                    throw writeFileThatExistsAndIsNotAFile(path.absolutePath)
                }
                throw writeToFile(this@UnixRegularFile, UnixErrorCode.last())
            }
            try {
                action(FileDescriptorBackedWriteStream(FileSource(path), WriteDescriptor(des)))
            } finally {
                close(des)
            }
        }
    }

    override fun <T> readBytes(action: (ReadStream) -> Result<T>): Result<T> {
        return memScoped {
            val des = open(path.absolutePath, O_RDONLY or O_NOFOLLOW or O_CLOEXEC)
            if (des < 0) {
                return if (errno == ENOENT) {
                    readFileThatDoesNotExist(path.absolutePath)
                } else {
                    readFile(this@UnixRegularFile, UnixErrorCode.last())
                }
            }
            try {
                action(FileDescriptorBackedReadStream(FileSource(path), ReadDescriptor(des)))
            } finally {
                close(des)
            }
        }
    }

    internal class FileSource(val path: AbsolutePath) : Source {
        override val displayName: String
            get() = "file $path"
    }
}

internal class UnixDirectory(path: AbsolutePath) : UnixFileSystemElement(path), Directory {
    override fun file(name: String): RegularFile {
        return UnixRegularFile(path.resolve(name))
    }

    override fun toDir(): Directory {
        return this
    }

    override fun dir(name: String): Directory {
        return UnixDirectory(path.resolve(name))
    }

    override fun symLink(name: String): SymLink {
        return UnixSymLink(path.resolve(name))
    }

    override fun deleteRecursively() {
        deleteRecursively(this) { entry ->
            val absolutePath = entry.absolutePath
            if (remove(absolutePath) != 0) {
                throw NativeException("Could not delete $path.")
            }
        }
    }

    override fun createTemporaryDirectory(): Directory {
        return createTempDir(this)
    }

    override fun createDirectories() {
        val parent = parent
        if (parent != null) {
            val metadata = parent.metadata()
            if (!metadata.directory) {
                // Error handling will deal with parent being a file, etc
                parent.createDirectories()
            }
        }
        createDir(this)
    }

    override fun listEntries(): Result<List<DirectoryEntry>> {
        val dirPointer = opendir(path.absolutePath)
        if (dirPointer == null) {
            if (errno == ENOENT) {
                return listDirectoryThatDoesNotExist(path.absolutePath)
            }
            if (errno == EPERM || errno == EACCES) {
                return UnreadableEntry(path.absolutePath)
            }
            if (errno == ENOTDIR) {
                return listDirectoryThatIsNotADirectory(path.absolutePath)
            }
            return listDirectory(this, UnixErrorCode.last())
        }
        try {
            val entries = mutableListOf<DirectoryEntry>()
            memScoped {
                while (true) {
                    val entryPointer = readdir(dirPointer)
                    if (entryPointer == null) {
                        break
                    }
                    val name = entryPointer.pointed.d_name.toKString()
                    if (name == "." || name == "..") {
                        continue
                    }
                    if (entryPointer.pointed.d_type.convert<Int>() == DT_DIR) {
                        entries.add(UnixDirectoryEntry(path, name, ElementType.Directory))
                    } else if (entryPointer.pointed.d_type.convert<Int>() == DT_LNK) {
                        entries.add(UnixDirectoryEntry(path, name, ElementType.SymLink))
                    } else if (entryPointer.pointed.d_type.convert<Int>() == DT_REG) {
                        entries.add(UnixDirectoryEntry(path, name, ElementType.RegularFile))
                    } else {
                        entries.add(UnixDirectoryEntry(path, name, ElementType.Other))
                    }
                }
            }
            return Success(entries)
        } finally {
            closedir(dirPointer)
        }
    }

    override fun visitTopDown(visitor: (DirectoryEntry) -> Unit) {
        visitTopDown(this, visitor)
    }
}


expect val canSetSymLinkPermissions: Boolean

internal class UnixSymLink(path: AbsolutePath) : UnixFileSystemElement(path), SymLink {

    override fun toSymLink(): SymLink {
        return this
    }

    override fun readSymLink(): Result<String> {
        memScoped {
            val size = fileSize()
            val buffer = ByteArray(size.convert())
            val count = readlink(path.absolutePath, buffer.refTo(0), size.convert())
            if (count < 0) {
                if (errno == EACCES) {
                    return UnreadableEntry(path.absolutePath)
                } else {
                    throw NativeException("Could not read symlink '$path'.")
                }
            }
            return Success(buffer.decodeToString(0, count.convert()))
        }
    }

    override fun writeSymLink(target: String) {
        val stat = metadata(path.absolutePath)
        if (stat is Success && stat.get() is SymlinkMetadata) {
            if (remove(path.absolutePath) < 0) {
                throw NativeException("Could not delete symlink $path.")
            }
        }
        if (symlink(target, path.absolutePath) < 0) {
            throw NativeException("Could not create symlink $path.")
        }
    }
}

private class UnixDirectoryEntry(private val parentPath: AbsolutePath, override val name: String, override val type: ElementType) : DirectoryEntry {
    override val path: AbsolutePath
        get() = parentPath.resolve(name)

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata(path.absolutePath).map { UnixSnapshot(path, it) }
    }

    override fun toDir(): Directory {
        return UnixDirectory(path)
    }

    override fun toFile(): RegularFile {
        return UnixRegularFile(path)
    }

    override fun toSymLink(): SymLink {
        return UnixSymLink(path)
    }
}

internal class UnixSnapshot(override val path: AbsolutePath, override val metadata: ElementMetadata) : AbstractElementSnapshot() {
    override fun asRegularFile(): RegularFile {
        return UnixRegularFile(path)
    }

    override fun asDirectory(): Directory {
        return UnixDirectory(path)
    }

    override fun asSymLink(): SymLink {
        return UnixSymLink(path)
    }
}

internal expect fun lastModified(stat: stat): Timestamp

internal expect fun mode(stat: stat): UInt

internal expect fun doOpen(path: String, flags: Int, mode: UInt): Int

internal fun createTempDir(baseDir: UnixDirectory): Directory {
    return memScoped {
        val pathCopy = baseDir.dir("dir-XXXXXX").absolutePath.cstr.ptr
        if (mkdtemp(pathCopy) == null) {
            throw NativeException("Could not create temporary directory in ${baseDir}.")
        }
        UnixDirectory(AbsolutePath(pathCopy.toKString()))
    }
}

@OptIn(UnsafeNumber::class)
internal fun createDir(dir: UnixDirectory) {
    memScoped {
        val result = mkdir(dir.path.absolutePath, S_IRWXU.convert())
        if (result != 0) {
            if (errno != EEXIST) {
                throw NativeException("Could not create directory $dir.")
            }
            val stat = metadata(dir.path.absolutePath)
            if (!stat.directory) {
                throw createDirectoryThatExistsAndIsNotADir(dir.path.absolutePath)
            }
        }
    }
}
