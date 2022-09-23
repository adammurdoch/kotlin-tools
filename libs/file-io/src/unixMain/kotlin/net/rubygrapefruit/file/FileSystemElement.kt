package net.rubygrapefruit.file

import kotlinx.cinterop.*
import platform.posix.*

internal actual fun stat(file: FileSystemElement): FileSystemElementMetadata {
    return memScoped {
        val statBuf = alloc<stat>()
        if (lstat(file.path, statBuf.ptr) != 0) {
            if (errno == ENOENT) {
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
        Directory(pwd.pointed.pw_dir!!.toKString())
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
        Directory(buffer.toKString())
    }
}

internal actual fun createTempDir(baseDir: Directory): Directory {
    return memScoped {
        val pathCopy = baseDir.dir("dir-XXXXXX").path.cstr.ptr
        if (mkdtemp(pathCopy) == null) {
            throw NativeException("Could not create temporary directory in ${baseDir}.")
        }
        Directory(pathCopy.toKString())
    }
}

internal actual fun createDir(dir: Directory) {
    memScoped {
        if (mkdir(dir.path, S_IRWXU) != 0) {
            if (errno != EEXIST) {
                throw NativeException("Could not create directory $dir.")
            }
        }
    }
}

internal actual fun writeToFile(file: RegularFile, text: String) {
    memScoped {
        val des = fopen(file.path, "w")
        if (des == null) {
            throw NativeException("Could not create file $file.")
        }
        try {
            if (fputs(text, des) == EOF) {
                throw NativeException("Could not write to file $file.")
            }
        } finally {
            fclose(des)
        }
    }
}

