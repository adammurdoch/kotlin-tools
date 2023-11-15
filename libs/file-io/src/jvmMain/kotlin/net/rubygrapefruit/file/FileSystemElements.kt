package net.rubygrapefruit.file

import java.io.File
import java.nio.file.Path

/**
 * Converts a [FileSystemElement] to a JVM [Path]
 */
fun FileSystemElement.path(): Path {
    return (this as JvmFileSystemElement).toPath()
}

/**
 * Converts a [FileSystemElement] to a JVM [File]
 */
fun FileSystemElement.file(): File {
    return (this as JvmFileSystemElement).toPath().toFile()
}
