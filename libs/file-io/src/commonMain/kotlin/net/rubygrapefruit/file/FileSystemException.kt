package net.rubygrapefruit.file

/**
 * An exception thrown when some operation on the file system fails.
 */
open class FileSystemException(message: String, cause: Throwable? = null): Exception(message, cause)
