package net.rubygrapefruit.file

import kotlinx.cinterop.*
import platform.posix.*

internal actual fun stat(file: String): FileSystemElementMetadata {
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

internal actual fun getUserHomeDir(): Directory {
    return memScoped {
        val uid = getuid()
        val pwd = getpwuid(uid)
        if (pwd == null) {
            throw NativeException("Could not get user home directory.")
        }
        NativeDirectory(pwd.pointed.pw_dir!!.toKString())
    }
}

internal actual fun getCurrentDir(): Directory {
    return memScoped {
        val length = MAXPATHLEN
        val buffer = allocArray<ByteVar>(length)
        val path = getcwd(buffer, length.convert())
        if (path == null) {
            throw NativeException("Could not get current directory.")
        }
        NativeDirectory(buffer.toKString())
    }
}

internal actual fun createTempDir(baseDir: NativeDirectory): Directory {
    return memScoped {
        val pathCopy = baseDir.dir("dir-XXXXXX").absolutePath.cstr.ptr
        if (mkdtemp(pathCopy) == null) {
            throw NativeException("Could not create temporary directory in ${baseDir}.")
        }
        NativeDirectory(pathCopy.toKString())
    }
}

internal actual fun createDir(dir: NativeDirectory) {
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

internal actual fun writeToFile(file: NativeRegularFile, text: String) {
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

internal actual fun readFromFile(file: NativeRegularFile): String {
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

