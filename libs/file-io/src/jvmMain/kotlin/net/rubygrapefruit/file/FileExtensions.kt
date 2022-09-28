package net.rubygrapefruit.file

import java.io.File

/**
 * Converts this JVM file to a `FileSystemElement`.
 */
fun File.toElement(): FileSystemElement = JvmFileSystemElement(absoluteFile.toPath().normalize())

/**
 * Converts this JVM file to a `Directory`
 */
fun File.toDir(): Directory = JvmDirectory(absoluteFile.toPath().normalize())