package net.rubygrapefruit.file

import net.rubygrapefruit.error.ErrorCode
import net.rubygrapefruit.error.NoErrorCode

internal fun missingElement(path: String, cause: Throwable? = null) = FileSystemException("File $path does not exist.", cause)

internal fun unreadableElement(path: String, cause: Throwable? = null) = FileSystemException("File $path is not readable.", cause)

internal fun createDirectoryThatExistsAndIsNotADir(path: String, cause: Throwable? = null) =
    FileSystemException("Could not create directory $path as it already exists but is not a directory.", cause)

internal fun createDirectory(path: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) =
    FileSystemException("Could not create directory $path.", errorCode, cause)

internal fun createDirectory(dir: Directory, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null): FileSystemException {
    var p = dir.parent
    while (p != null) {
        val metadata = p.metadata()
        if (metadata is MissingEntry) {
            // Keep looking
            p = p.parent
            continue
        }
        if (metadata.directory) {
            // Found a directory - should have been able to create dir so rethrow original failure
            throw createDirectory(dir.absolutePath, errorCode, cause)
        }
        // Found something else - fail
        throw createDirectoryThatExistsAndIsNotADir(p.absolutePath, cause)
    }
    // Nothing in the hierarchy exists, which is unexpected, so rethrow original failure
    throw createDirectory(dir.absolutePath, errorCode, cause)
}

internal fun listDirectoryThatDoesNotExist(path: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) =
    MissingDirectoryException("Could not list directory $path as it does not exist.", errorCode, cause)

internal fun listDirectoryThatIsNotReadable(path: String) = DirectoryPermissionException("Directory $path is not readable.")

internal fun listDirectoryThatIsNotADirectory(path: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) =
    NotADirectoryException("Could not list directory $path as it is not a directory.", errorCode, cause)

internal fun writeFileThatExistsAndIsNotAFile(path: String, cause: Throwable? = null) = FileSystemException("Could not write to $path as it is not a file.", cause)

internal fun writeFileInDirectoryThatDoesNotExist(path: String, ancestor: String, cause: Throwable? = null) =
    FileSystemException("Could not write to $path as directory $ancestor does not exist.", cause)

internal fun writeFileInDirectoryThatIsNotADir(path: String, ancestor: String, cause: Throwable? = null) =
    FileSystemException("Could not write to $path as $ancestor exists but is not a directory.", cause)

internal fun readFileThatDoesNotExist(path: String, cause: Throwable? = null) =
    FileSystemException("Could not read from file $path as it does not exist.", cause)

internal fun readFileThatIsNotAFile(path: String, cause: Throwable? = null) =
    FileSystemException("Could not read from file $path as it is not a file.", cause)

internal fun readFileInDirectoryThatDoesNotExist(path: String, ancestor: String, cause: Throwable? = null) =
    FileSystemException("Could not read from $path as directory $ancestor does not exist.", cause)

internal fun readFileInDirectoryThatIsNotADir(path: String, ancestor: String, cause: Throwable? = null) =
    FileSystemException("Could not read from $path as $ancestor exists but is not a directory.", cause)

internal fun openFileThatIsNotAFile(path: String, cause: Throwable? = null) =
    FileSystemException("Could not open file $path as it is not a file.", cause)

internal fun openFileInDirectoryThatDoesNotExist(path: String, ancestor: String, cause: Throwable? = null) =
    FileSystemException("Could not open file $path as directory $ancestor does not exist.", cause)

internal fun openFileInDirectoryThatIsNotADir(path: String, ancestor: String, cause: Throwable? = null) =
    FileSystemException("Could not open file $path as $ancestor exists but is not a directory.", cause)

internal fun deleteFileThatIsNotAFile(path: String, cause: Throwable? = null) = FileSystemException("Could not delete file $path as it is not a file.", cause)

internal fun deleteFile(path: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) = FileSystemException("Could not delete file $path.", errorCode, cause)

internal fun deleteElementThatIsNotWritable(path: String, parent: String, cause: Throwable? = null) =
    FileSystemException("Could not delete file $path as directory $parent is not writable.", cause)

internal fun deleteElement(path: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) = FileSystemException("Could not delete file $path.", errorCode, cause)

internal fun notSupported(path: String, operation: String) = FileSystemException("Could not $operation $path as it is not supported by this filesystem.")

internal fun unreadableSymlink(path: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) =
    SymlinkPermissionException("Symlink $path is not readable.", errorCode, cause)

internal fun readMissingSymlink(path: String) = FileSystemException("Could not read symlink $path as it does not exist.")

internal fun setPermissionsNotSupported(path: String) = notSupported(path, "set POSIX permissions on")

internal fun setPermissionsOnMissingElement(path: String) = FileSystemException("Could not set POSIX permissions on $path as it does not exist.")

internal fun setPermissions(path: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) =
    FileSystemException("Could not set POSIX permissions on $path.", errorCode, cause)

internal fun readPermissionNotSupported(path: String) = UnsupportedOperation<Any>(path, "read POSIX permissions for").failure

internal fun readPermissionOnMissingElement(path: String) = MissingEntry<Any> { FileSystemException("Could not read POSIX permissions for $path as it does not exist.") }.failure

internal fun readPermission(path: String, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null) =
    FailedOperation<Any>(FileSystemException("Could not read POSIX permissions for $path.", errorCode, cause)).failure

/**
 * Tries to infer why a directory could not be listed.
 */
internal fun listDirectory(directory: Directory, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null): FileSystemException {
    val metadata = directory.metadata()
    return if (!metadata.missing) {
        listDirectoryThatIsNotADirectory(directory.absolutePath, errorCode, cause)
    } else if (metadata.directory) {
        FileSystemException("Could not list directory ${directory.absolutePath}.", errorCode, cause)
    } else {
        listDirectoryThatDoesNotExist(directory.absolutePath, errorCode, cause)
    }
}

/**
 * Tries to infer why a directory could not be deleted.
 */
internal fun deleteDirectory(directory: Directory, cause: Throwable? = null): FileSystemException {
    val metadata = directory.metadata()
    return if (!metadata.missing) {
        FileSystemException("Could not delete directory ${directory.absolutePath} as it is not a directory.", cause)
    } else {
        FileSystemException("Could not delete directory ${directory.absolutePath}.", cause)
    }
}

/**
 * Tries to infer why a file could not be opened.
 */
internal fun openFile(file: RegularFile, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null): FileSystemException {
    val fileMetadata = file.metadata()
    return if (fileMetadata.regularFile) {
        FileSystemException("Could not open ${file.absolutePath}", errorCode, cause)
    } else if (fileMetadata.missing) {
        var lastMissing: Directory? = null
        var p: Directory? = file.parent
        while (p != null) {
            val parentMetadata = p.metadata()
            if (parentMetadata.missing) {
                lastMissing = p
            } else if (parentMetadata is Success) {
                if (parentMetadata.get() is DirectoryMetadata) {
                    if (lastMissing != null) {
                        return openFileInDirectoryThatDoesNotExist(file.absolutePath, lastMissing.absolutePath, cause)
                    } else {
                        break
                    }
                } else {
                    return openFileInDirectoryThatIsNotADir(file.absolutePath, p.absolutePath, cause)
                }
            }
            p = p.parent
        }
        FileSystemException("Could not open ${file.absolutePath}", errorCode, cause)
    } else {
        openFileThatIsNotAFile(file.absolutePath, cause)
    }
}

/**
 * Tries to infer why a file could not be read.
 */
internal fun readFile(file: RegularFile, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null): FileSystemException {
    val fileMetadata = file.metadata()
    return if (fileMetadata.regularFile) {
        FileSystemException("Could not read from ${file.absolutePath}", errorCode, cause)
    } else if (fileMetadata.missing) {
        var lastMissing: Directory? = null
        var p: Directory? = file.parent
        while (p != null) {
            val parentMetadata = p.metadata()
            if (parentMetadata.missing) {
                lastMissing = p
            } else if (parentMetadata is Success) {
                if (parentMetadata.get() is DirectoryMetadata) {
                    if (lastMissing != null) {
                        return readFileInDirectoryThatDoesNotExist(file.absolutePath, lastMissing.absolutePath, cause)
                    } else {
                        break
                    }
                } else {
                    return readFileInDirectoryThatIsNotADir(file.absolutePath, p.absolutePath, cause)
                }
            }
            p = p.parent
        }
        readFileThatDoesNotExist(file.absolutePath, cause)
    } else {
        readFileThatIsNotAFile(file.absolutePath, cause)
    }
}

/**
 * Tries to infer why a file could not be written to.
 */
internal fun writeToFile(file: RegularFile, errorCode: ErrorCode = NoErrorCode, cause: Throwable? = null): FileSystemException {
    val fileMetadata = file.metadata()
    if (fileMetadata.regularFile) {
        return FileSystemException("Could not write to ${file.absolutePath}", errorCode, cause)
    }
    if (fileMetadata.missing) {
        var lastMissing: Directory? = null
        var p: Directory? = file.parent
        while (p != null) {
            val parentMetadata = p.metadata()
            if (parentMetadata.missing) {
                lastMissing = p
            } else if (parentMetadata is Success) {
                if (parentMetadata.get() is DirectoryMetadata) {
                    if (lastMissing != null) {
                        return writeFileInDirectoryThatDoesNotExist(file.absolutePath, lastMissing.absolutePath, cause)
                    } else {
                        break
                    }
                } else {
                    return writeFileInDirectoryThatIsNotADir(file.absolutePath, p.absolutePath, cause)
                }
            }
            p = p.parent
        }
        return FileSystemException("Could not write to ${file.absolutePath}", errorCode, cause)
    }
    return writeFileThatExistsAndIsNotAFile(file.absolutePath, cause)
}