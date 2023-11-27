@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
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
        return stat(path.absolutePath)
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata().map { SnapshotImpl(path, it) }
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
                throw NativeException("Could not stat $path.")
            }
            Success(posixPermissions(statBuf))
        }
    }

    @OptIn(UnsafeNumber::class)
    override fun setPermissions(permissions: PosixPermissions) {
        if (lchmod(path.absolutePath, permissions.mode.convert()) != 0) {
            if (errno == ENOTSUP) {
                throw setPermissionsNotSupported(path.absolutePath)
            } else {
                throw NativeException("Could not set permissions on $path.")
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
        if (remove(path.absolutePath) != 0) {
            throw NativeException("Could not delete $path.")
        }
    }

    override fun writeBytes(bytes: ByteArray) {
        writing { file ->
            fwrite(bytes.refTo(0), 1.convert(), bytes.size.convert(), file)
        }
    }

    override fun writeText(text: String) {
        writing { file ->
            if (fputs(text, file) == EOF) {
                if (errno == ENOTDIR) {
                    throw fileExistsAndIsNotAFile(path.absolutePath)
                }
                val errnoValue = errno
                throw writeToFile(this@UnixRegularFile, null) { message, _ -> NativeException(message, errnoValue) }
            }
        }
    }

    private fun writing(action: (CPointer<FILE>) -> Unit) {
        memScoped {
            val des = fopen(path.absolutePath, "w")
            if (des == null) {
                if (errno == EISDIR) {
                    throw fileExistsAndIsNotAFile(path.absolutePath)
                }
                val errnoValue = errno
                throw writeToFile(this@UnixRegularFile, null) { message, _ -> NativeException(message, errnoValue) }
            }
            try {
                action(des)
            } finally {
                fclose(des)
            }
        }
    }

    override fun readBytes(): Result<ByteArray> {
        return reading { buffer, len -> buffer.sliceArray(0 until len.toInt()) }
    }

    override fun readText(): Result<String> {
        return reading { buffer, len -> buffer.decodeToString(0, len.convert()) }
    }

    private fun <T> reading(action: (ByteArray, Long) -> T): Result<T> {
        return memScoped {
            val des = open(path.absolutePath, O_RDONLY)
            if (des < 0) {
                throw NativeException("Could not open $path.")
            }
            try {
                val fileSize = fileSize()
                val buffer = ByteArray(fileSize.convert())
                val nread = read(des, buffer.refTo(0), fileSize.convert())
                if (nread < 0) {
                    throw NativeException("Could not read from $path.")
                }
                Success(action(buffer, nread))
            } finally {
                close(des)
            }
        }
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
                return MissingEntry(path.absolutePath)
            }
            if (errno == EPERM || errno == EACCES) {
                return UnreadableEntry(path.absolutePath)
            }
            throw NativeException("Could not list directory '$path'.")
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
        val stat = stat(path.absolutePath)
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
        return stat(path.absolutePath).map { SnapshotImpl(path, it) }
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

internal class SnapshotImpl(override val path: AbsolutePath, override val metadata: ElementMetadata) : AbstractElementSnapshot() {
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

@OptIn(UnsafeNumber::class)
internal fun stat(file: String): Result<ElementMetadata> {
    return memScoped {
        val statBuf = alloc<stat>()
        if (lstat(file, statBuf.ptr) != 0) {
            if (errno == ENOENT || errno == ENOTDIR) {
                return MissingEntry(file)
            }
            if (errno == EACCES) {
                return UnreadableEntry(file)
            } else {
                throw NativeException("Could not stat file '$file'.")
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

internal expect fun lastModified(stat: stat): Timestamp

internal expect fun mode(stat: stat): UInt

private fun posixPermissions(stat: stat): PosixPermissions {
    return PosixPermissions((mode(stat) and (S_IRWXU or S_IRWXG or S_IRWXO).convert()))
}

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
            val stat = stat(dir.path.absolutePath)
            if (!stat.directory) {
                throw directoryExistsAndIsNotADir(dir.path.absolutePath)
            }
        }
    }
}
