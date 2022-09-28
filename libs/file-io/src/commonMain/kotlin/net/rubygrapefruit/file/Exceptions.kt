package net.rubygrapefruit.file

internal fun missingElement(path: String, cause: Throwable? = null) = FileSystemException("File $path does not exist.", cause)

internal fun unreadableElement(path: String, cause: Throwable? = null) = FileSystemException("File $path is not readable.", cause)

internal fun directoryExistsAndIsNotADir(path: String, cause: Throwable? = null) = FileSystemException("Could not create directory $path as it already exists but is not a directory.", cause)

internal fun createDirectory(path: String, cause: Throwable? = null) = FileSystemException("Could not create directory $path.", cause)

internal fun fileExistsAndIsNotAFile(path: String, cause: Throwable? = null) = FileSystemException("Could not write to $path as it already exists but is not a file.", cause)

internal fun parentDirectoryDoesNotExist(path: String, ancestor: String, cause: Throwable? = null) = FileSystemException("Could not write to $path as directory $ancestor does not exist.", cause)

internal fun parentDirectoryIsNotADir(path: String, ancestor: String, cause: Throwable? = null) = FileSystemException("Could not write to $path as $ancestor exists but is not a directory.", cause)

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
                        return parentDirectoryDoesNotExist(file.absolutePath, lastMissing.absolutePath, cause)
                    } else {
                        break
                    }
                } else {
                    return parentDirectoryIsNotADir(file.absolutePath, p.absolutePath, cause)
                }
            }
            p = p.parent
        }
        return factory("Could not write to ${file.absolutePath}", cause)
    }
    return fileExistsAndIsNotAFile(file.absolutePath, cause)
}