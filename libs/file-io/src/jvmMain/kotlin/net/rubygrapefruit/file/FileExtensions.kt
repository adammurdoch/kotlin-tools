package net.rubygrapefruit.file

import java.io.File
import java.nio.file.Path

/**
 * Converts this JVM file to a `FileSystemElement`.
 */
fun File.toElement(): FileSystemElement = JvmFileSystemElement(absoluteFile.toPath().normalize())

/**
 * Converts this JVM file to a `Directory`
 */
fun File.toDir(): Directory = JvmDirectory(absoluteFile.toPath().normalize())

/**
 * Converts this JVM path to a `FileSystemElement`.
 */
fun Path.toElement(): FileSystemElement = JvmFileSystemElement(toAbsolutePath().normalize())

/**
 * Converts this JVM path to a `Directory`
 */
fun Path.toDir(): Directory = JvmDirectory(toAbsolutePath().normalize())
