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

    override fun metadata(): FileSystemElementMetadata {
        return stat(path)
    }

    override fun resolve(): FileResolveResult {
        return ResolveResultImpl(path, metadata())
    }

}

internal class UnixRegularFile(path: String) : UnixFileSystemElement(path), RegularFile {
    override fun writeText(text: String) {
        writeToFile(this, text)
    }

    override fun readText(): String {
        return readFromFile(this)
    }
}

internal class UnixDirectory(path: String) : UnixFileSystemElement(path), Directory {
    override fun file(name: String): RegularFile {
        return UnixRegularFile(resolveName(name))
    }

    override fun dir(name: String): Directory {
        return UnixDirectory(resolveName(name))
    }

    override fun createTemporaryDirectory(): Directory {
        return createTempDir(this)
    }

    override fun createDirectories() {
        val parent = parent
        if (parent != null) {
            if (parent.metadata() != DirectoryMetadata) {
                // Error handling will deal with parent being a file, etc
                parent.createDirectories()
            }
        }
        createDir(this)
    }

    override fun resolve(name: String): FileResolveResult {
        val path = resolveName(name)
        return ResolveResultImpl(path, stat(path))
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

private class ResolveResultImpl(override val absolutePath: String, override val metadata: FileSystemElementMetadata) : AbstractFileResolveResult() {
    override fun asRegularFile(): RegularFile {
        return UnixRegularFile(absolutePath)
    }

    override fun asDirectory(): Directory {
        return UnixDirectory(absolutePath)
    }
}

internal fun stat(file: String): FileSystemElementMetadata {
    return memScoped {
        val statBuf = alloc<stat>()
        if (lstat(file, statBuf.ptr) != 0) {
            if (errno == ENOENT || errno == ENOTDIR) {
                return MissingEntryMetadata
            }
            if (errno == EACCES) {
                UnreadableEntryMetadata
            } else {
                throw NativeException("Could not stat file '$file'.")
            }
        } else if (statBuf.st_mode.convert<Int>() and S_IFDIR == S_IFDIR) {
            DirectoryMetadata
        } else if (statBuf.st_mode.convert<Int>() and S_IFLNK == S_IFLNK) {
            SymlinkMetadata
        } else if (statBuf.st_mode.convert<Int>() and S_IFLNK == S_IFREG) {
            RegularFileMetadata(statBuf.st_size.convert())
        } else {
            OtherMetadata
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
            } else if (stat(dir.path) != DirectoryMetadata) {
                throw directoryExistsAndIsNotADir(dir.path)
            }
        }
    }
}

internal fun writeToFile(file: UnixRegularFile, text: String) {
    memScoped {
        val des = fopen(file.path, "w")
        if (des == null) {
            if (errno == EISDIR) {
                throw fileExistsAndIsNotAFile(file.path)
            }
            val errnoValue = errno
            throw writeToFile(file, null) { message, _ -> NativeException(message, errnoValue) }
        }
        try {
            if (fputs(text, des) == EOF) {
                if (errno == ENOTDIR) {
                    throw fileExistsAndIsNotAFile(file.path)
                }
                val errnoValue = errno
                throw writeToFile(file, null) { message, _ -> NativeException(message, errnoValue) }
            }
        } finally {
            fclose(des)
        }
    }
}

internal fun readFromFile(file: UnixRegularFile): String {
    return memScoped {
        val des = open(file.path, O_RDONLY)
        if (des < 0) {
            throw NativeException("Could not open $file")
        }
        try {
            val statBuf = alloc<stat>()
            if (stat(file.absolutePath, statBuf.ptr) != 0) {
                throw NativeException("Could not stat $file")
            }
            val fileSize = statBuf.st_size
            val buffer = ByteArray(fileSize.convert())
            val nread = read(des, buffer.refTo(0), fileSize.convert())
            if (nread < 0) {
                throw NativeException("Could not read from $file")
            }
            buffer.decodeToString(0, nread.convert())
        } finally {
            close(des)
        }
    }
}

