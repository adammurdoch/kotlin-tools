package net.rubygrapefruit.file

import java.io.File
import java.nio.file.Path

/**
 * Converts a [FileSystemElement] to a JVM [Path]
 */
fun FileSystemElement.toPath(): Path {
    return (this as JvmFileSystemElement).toPath()
}

/**
 * Converts a [FileSystemElement] to a JVM [File]
 */
fun FileSystemElement.toFile(): File {
    return (this as JvmFileSystemElement).toFile()
}
