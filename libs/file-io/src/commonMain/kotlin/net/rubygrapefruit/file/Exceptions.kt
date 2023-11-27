package net.rubygrapefruit.file

internal fun missingElement(path: String, cause: Throwable? = null) = FileSystemException("File $path does not exist.", cause)

internal fun unreadableElement(path: String, cause: Throwable? = null) = FileSystemException("File $path is not readable.", cause)

internal fun createDirectoryThatExistsAndIsNotADir(path: String, cause: Throwable? = null) = FileSystemException("Could not create directory $path as it already exists but is not a directory.", cause)

internal fun createDirectory(path: String, cause: Throwable? = null) = FileSystemException("Could not create directory $path.", cause)

internal fun writeFileThatExistsAndIsNotAFile(path: String, cause: Throwable? = null) = FileSystemException("Could not write to $path as it is not a file.", cause)

internal fun writeFileInDirectoryThatDoesNotExist(path: String, ancestor: String, cause: Throwable? = null) = FileSystemException("Could not write to $path as directory $ancestor does not exist.", cause)

internal fun writeFileInDirectoryThatIsNotADir(path: String, ancestor: String, cause: Throwable? = null) = FileSystemException("Could not write to $path as $ancestor exists but is not a directory.", cause)

internal fun deleteFileThatIsNotAFile(path: String, cause: Throwable? = null) = FileSystemException("Could not delete $path as it is not a file.", cause)

internal fun <T> readFileThatDoesNotExist(path: String, cause: Throwable? = null) = MissingEntry<T>(path, cause)

internal fun deleteFile(path: String, cause: Throwable? = null) = FileSystemException("Could not delete $path.", cause)

internal fun notSupported(path: String, operation: String) = FileSystemException("Could not $operation $path as it is not supported by this filesystem.")

internal fun setPermissionsNotSupported(path: String) = notSupported(path, "set permissions on")

/**
 * Tries to infer why a file could not be read .
 */
internal fun <T> readFile(file: RegularFile, cause: Throwable? = null): Failed<T> {
    val fileMetadata = file.metadata()
    return if (fileMetadata.regularFile) {
        FailedOperation(FileSystemException("Could not read from ${file.absolutePath}", cause))
    } else if (fileMetadata.missing) {
        readFileThatDoesNotExist(file.absolutePath, cause)
    } else {
        FailedOperation(FileSystemException("Could not read from ${file.absolutePath} as it is not a file.", cause))
    }
}

/**
 * Tries to infer why a file could not be written to.
 */
internal fun writeToFile(file: RegularFile, cause: Throwable? = null, factory: (String, Throwable?) -> FileSystemException = { m, c -> FileSystemException(m, c) }): FileSystemException {
    val fileMetadata = file.metadata()
    if (fileMetadata.regularFile) {
        return factory("Could not write to ${file.absolutePath}", cause)
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
        return factory("Could not write to ${file.absolutePath}", cause)
    }
    return writeFileThatExistsAndIsNotAFile(file.absolutePath, cause)
}