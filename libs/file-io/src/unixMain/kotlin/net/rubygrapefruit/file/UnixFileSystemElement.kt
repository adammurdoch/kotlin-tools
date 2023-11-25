@file:OptIn(ExperimentalForeignApi::class)

package net.rubygrapefruit.file

import kotlinx.cinterop.*
import platform.posix.*

internal open class UnixFileSystemElement(path: String) : NativeFileSystemElement(path) {
    override val parent: Directory?
        get() {
            return if (absolutePath == "/") {
                null
            } else {
                UnixDirectory(absolutePath.substringBeforeLast("/"))
            }
        }

    override fun metadata(): Result<ElementMetadata> {
        return stat(path.absolutePath)
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata().map { SnapshotImpl(path, it) }
    }

    override fun toDir(): Directory {
        return UnixDirectory(absolutePath)
    }

    override fun toFile(): RegularFile {
        return UnixRegularFile(absolutePath)
    }

    override fun toSymLink(): SymLink {
        return UnixSymLink(absolutePath)
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
        return if (capability == FileSystemCapability.SetSymLinkPosixPermissions) {
            canSetSymLinkPermissions
        } else {
            true
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

internal class UnixRegularFile(path: String) : UnixFileSystemElement(path), RegularFile {
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

internal class UnixDirectory(path: String) : UnixFileSystemElement(path), Directory {
    override fun file(name: String): RegularFile {
        return UnixRegularFile(resolveName(name))
    }

    override fun dir(name: String): Directory {
        return UnixDirectory(resolveName(name))
    }

    override fun symLink(name: String): SymLink {
        return UnixSymLink(resolveName(name))
    }

    override fun deleteRecursively() {
        if (remove(path.absolutePath) != 0) {
            throw NativeException("Could not delete $path.")
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

    override fun resolve(name: String): ElementPath {
        val path = resolveName(name)
        return AbsolutePath(path)
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
                        entries.add(DirectoryEntryImpl(path.absolutePath, name, ElementType.Directory))
                    } else if (entryPointer.pointed.d_type.convert<Int>() == DT_LNK) {
                        entries.add(DirectoryEntryImpl(path.absolutePath, name, ElementType.SymLink))
                    } else if (entryPointer.pointed.d_type.convert<Int>() == DT_REG) {
                        entries.add(DirectoryEntryImpl(path.absolutePath, name, ElementType.RegularFile))
                    } else {
                        entries.add(DirectoryEntryImpl(path.absolutePath, name, ElementType.Other))
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

    private fun resolveName(name: String): String {
        return if (name.startsWith("/")) {
            name
        } else if (name == ".") {
            path.absolutePath
        } else if (name.startsWith("./")) {
            resolveName(name.substring(2))
        } else if (name == "..") {
            parent!!.absolutePath
        } else if (name.startsWith("../")) {
            (parent as UnixDirectory).resolveName(name.substring(3))
        } else {
            "${path.absolutePath}/$name"
        }
    }
}


expect val canSetSymLinkPermissions: Boolean

internal class UnixSymLink(path: String) : UnixFileSystemElement(path), SymLink {
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

private class DirectoryEntryImpl(private val parentPath: String, override val name: String, override val type: ElementType) : DirectoryEntry {
    override val path: AbsolutePath
        get() = AbsolutePath("$parentPath/$name")

    override fun snapshot(): Result<ElementSnapshot> {
        return stat(path.absolutePath).map { SnapshotImpl(path, it) }
    }

    override fun toDir(): Directory {
        return UnixDirectory(path.absolutePath)
    }

    override fun toFile(): RegularFile {
        return UnixRegularFile(path.absolutePath)
    }

    override fun toSymLink(): SymLink {
        return UnixSymLink(path.absolutePath)
    }
}

internal class SnapshotImpl(override val path: AbsolutePath, override val metadata: ElementMetadata) : AbstractElementSnapshot() {
    override fun asRegularFile(): RegularFile {
        return UnixRegularFile(absolutePath)
    }

    override fun asDirectory(): Directory {
        return UnixDirectory(absolutePath)
    }

    override fun asSymLink(): SymLink {
        return UnixSymLink(absolutePath)
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
        UnixDirectory(pwd.pointed.pw_dir!!.toKString())
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
        UnixDirectory(buffer.toKString())
    }
}

internal fun createTempDir(baseDir: UnixDirectory): Directory {
    return memScoped {
        val pathCopy = baseDir.dir("dir-XXXXXX").absolutePath.cstr.ptr
        if (mkdtemp(pathCopy) == null) {
            throw NativeException("Could not create temporary directory in ${baseDir}.")
        }
        UnixDirectory(pathCopy.toKString())
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
