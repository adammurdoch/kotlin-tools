package net.rubygrapefruit.file

internal fun missingElement(path: String, cause: Throwable? = null) = FileSystemException("File $path does not exist.", cause)

internal fun unreadableElement(path: String, cause: Throwable? = null) = FileSystemException("File $path is not readable.", cause)

internal fun createDirectoryThatExistsAndIsNotADir(path: String, cause: Throwable? = null) =
    FileSystemException("Could not create directory $path as it already exists but is not a directory.", cause)

internal fun createDirectory(path: String, cause: Throwable? = null) = FileSystemException("Could not create directory $path.", cause)

internal fun <T> listDirectoryThatDoesNotExist(path: String, cause: Throwable? = null) =
    MissingEntry<T> { FileSystemException("Could not list directory $path as it does not exist.", cause) }

internal fun writeFileThatExistsAndIsNotAFile(path: String, cause: Throwable? = null) = FileSystemException("Could not write to $path as it is not a file.", cause)

internal fun writeFileInDirectoryThatDoesNotExist(path: String, ancestor: String, cause: Throwable? = null) =
    FileSystemException("Could not write to $path as directory $ancestor does not exist.", cause)

internal fun writeFileInDirectoryThatIsNotADir(path: String, ancestor: String, cause: Throwable? = null) =
    FileSystemException("Could not write to $path as $ancestor exists but is not a directory.", cause)

internal fun <T> readFileThatDoesNotExist(path: String, cause: Throwable? = null) =
    MissingEntry<T> { FileSystemException("Could not read from file $path as it does not exist.", cause) }

internal fun <T> readFileThatIsNotAFile(path: String, cause: Throwable? = null) =
    FailedOperation<T>(FileSystemException("Could not read from file $path as it is not a file.", cause))

internal fun deleteFileThatIsNotAFile(path: String, cause: Throwable? = null) = FileSystemException("Could not delete file $path as it is not a file.", cause)

internal fun deleteFile(path: String, cause: Throwable? = null) = FileSystemException("Could not delete file $path.", cause)

internal fun notSupported(path: String, operation: String) = FileSystemException("Could not $operation $path as it is not supported by this filesystem.")

internal fun setPermissionsNotSupported(path: String) = notSupported(path, "set permissions on")

/**
 * Tries to infer why a directory could not be listed.
 */
internal fun <T> listDirectory(directory: Directory, cause: Throwable? = null): Failed<T> {
    val metadata = directory.metadata()
    return if (!metadata.missing) {
        FailedOperation(FileSystemException("Could not list directory ${directory.absolutePath} as it is not a directory.", cause))
    } else {
        FailedOperation(FileSystemException("Could not list directory ${directory.absolutePath}.", cause))
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
 * Tries to infer why a file could not be read.
 */
internal fun <T> readFile(file: RegularFile, cause: Throwable? = null): Failed<T> {
    val fileMetadata = file.metadata()
    return if (fileMetadata.regularFile) {
        FailedOperation(FileSystemException("Could not read from ${file.absolutePath}", cause))
    } else if (fileMetadata.missing) {
        readFileThatDoesNotExist(file.absolutePath, cause)
    } else {
        readFileThatIsNotAFile(file.absolutePath, cause)
    }
}

/**
 * Tries to infer why a file could not be written to.
 */
internal fun writeToFile(
    file: RegularFile,
    errorCode: ErrorCode = NoErrorCode,
    cause: Throwable? = null,
): FileSystemException {
    val fileMetadata = file.metadata()
    if (fileMetadata.regularFile) {
        return FileSystemException("Could not write to ${file.absolutePath}", errorCode, cause)
    }
    if (fileMetadata.missing) {
        var lastMissing: Directory? = null
        var p = file.parent
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