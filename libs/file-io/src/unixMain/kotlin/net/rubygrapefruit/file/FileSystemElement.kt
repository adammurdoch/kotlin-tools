package net.rubygrapefruit.file

import kotlinx.cinterop.*
import platform.posix.*

internal sealed class UnixFileSystemElement(path: String) : PathFileSystemElement(path) {
    override val parent: Directory?
        get() {
            return if (path == "/") {
                null
            } else {
                UnixDirectory(path.substringBeforeLast("/"))
            }
        }

    override fun metadata(): Result<ElementMetadata> {
        return stat(path)
    }

    override fun snapshot(): Result<ElementSnapshot> {
        return metadata().map { SnapshotImpl(path, it) }
    }

    protected fun MemScope.fileSize(): Long {
        val statBuf = alloc<stat>()
        if (lstat(path, statBuf.ptr) != 0) {
            throw NativeException("Could not stat $path.")
        }
        return statBuf.st_size
    }

}

internal class UnixRegularFile(path: String) : UnixFileSystemElement(path), RegularFile {
    override fun writeText(text: String) {
        memScoped {
            val des = fopen(path, "w")
            if (des == null) {
                if (errno == EISDIR) {
                    throw fileExistsAndIsNotAFile(path)
                }
                val errnoValue = errno
                throw writeToFile(this@UnixRegularFile, null) { message, _ -> NativeException(message, errnoValue) }
            }
            try {
                if (fputs(text, des) == EOF) {
                    if (errno == ENOTDIR) {
                        throw fileExistsAndIsNotAFile(path)
                    }
                    val errnoValue = errno
                    throw writeToFile(this@UnixRegularFile, null) { message, _ -> NativeException(message, errnoValue) }
                }
            } finally {
                fclose(des)
            }
        }
    }

    override fun readText(): Result<String> {
        return memScoped {
            val des = open(path, O_RDONLY)
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
                Success(buffer.decodeToString(0, nread.convert()))
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

    override fun createTemporaryDirectory(): Directory {
        return createTempDir(this)
    }

    override fun createDirectories() {
        val parent = parent
        if (parent != null) {
            val metadata = parent.metadata()
            if (metadata !is Success || metadata.get() != DirectoryMetadata) {
                // Error handling will deal with parent being a file, etc
                parent.createDirectories()
            }
        }
        createDir(this)
    }

    override fun resolve(name: String): FileSystemElement {
        val path = resolveName(name)
        return UnixRegularFile(path)
    }

    override fun listEntries(): Result<List<DirectoryEntry>> {
        val dirPointer = opendir(path)
        if (dirPointer == null) {
            if (errno == ENOENT) {
                return MissingEntry(path)
            }
            if (errno == EPERM || errno == EACCES) {
                return UnreadableEntry(path)
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
                        entries.add(DirectoryEntryImpl(name, ElementType.Directory))
                    } else if (entryPointer.pointed.d_type.convert<Int>() == DT_LNK) {
                        entries.add(DirectoryEntryImpl(name, ElementType.SymLink))
                    } else if (entryPointer.pointed.d_type.convert<Int>() == DT_REG) {
                        entries.add(DirectoryEntryImpl(name, ElementType.RegularFile))
                    } else {
                        entries.add(DirectoryEntryImpl(name, ElementType.Other))
                    }
                }
            }
            return Success(entries)
        } finally {
            closedir(dirPointer)
        }
    }

    private fun resolveName(name: String): String {
        if (name.startsWith("/")) {
            return name
        } else if (name == ".") {
            return path
        } else if (name.startsWith("./")) {
            return resolveName(name.substring(2))
        } else if (name == "..") {
            return parent!!.absolutePath
        } else if (name.startsWith("../")) {
            return (parent as UnixDirectory).resolveName(name.substring(3))
        } else {
            return "$path/$name"
        }
    }
}

internal class UnixSymLink(path: String) : UnixFileSystemElement(path), SymLink {
    override fun readSymLink(): Result<String> {
        memScoped {
            val size = fileSize()
            val buffer = ByteArray(size.convert())
            val nread = readlink(path, buffer.refTo(0), size.convert())
            if (nread < 0) {
                throw NativeException("Could not read symlink $path.")
            }
            return Success(buffer.decodeToString(0, nread.convert()))
        }
    }

    override fun writeSymLink(target: String) {
        val stat = stat(path)
        if (stat is Success && stat.get() is SymlinkMetadata) {
            if (remove(path) < 0) {
                throw NativeException("Could not delete symlink $path.")
            }
        }
        if (symlink(target, path) < 0) {
            throw NativeException("Could not create symlink $path.")
        }
    }
}

private class DirectoryEntryImpl(override val name: String, override val type: ElementType) : DirectoryEntry {
}

private class SnapshotImpl(override val absolutePath: String, override val metadata: ElementMetadata) : AbstractElementSnapshot() {
    override fun asRegularFile(): RegularFile {
        return UnixRegularFile(absolutePath)
    }

    override fun asDirectory(): Directory {
        return UnixDirectory(absolutePath)
    }
}

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
        } else if (statBuf.st_mode.convert<Int>() and S_IFDIR == S_IFDIR) {
            Success(DirectoryMetadata)
        } else if (statBuf.st_mode.convert<Int>() and S_IFLNK == S_IFLNK) {
            Success(SymlinkMetadata)
        } else if (statBuf.st_mode.convert<Int>() and S_IFLNK == S_IFREG) {
            Success(RegularFileMetadata(statBuf.st_size.convert()))
        } else {
            Success(OtherMetadata)
        }
    }
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

internal fun createDir(dir: UnixDirectory) {
    memScoped {
        val result = mkdir(dir.path, S_IRWXU)
        if (result != 0) {
            if (errno != EEXIST) {
                throw NativeException("Could not create directory $dir.")
            }
            val stat = stat(dir.path)
            if (stat !is Success || stat.get() != DirectoryMetadata) {
                throw directoryExistsAndIsNotADir(dir.path)
            }
        }
    }
}
