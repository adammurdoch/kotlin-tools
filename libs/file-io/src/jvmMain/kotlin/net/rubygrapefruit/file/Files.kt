package net.rubygrapefruit.file

import java.io.File

/**
 * Converts this JVM [File] to an [ElementPath]
 */
fun File.resolve(): ElementPath = JvmElementPath(absoluteFile.toPath().normalize())

/**
 * Converts this JVM file to a `Directory`
 */
fun File.toDir(): Directory = JvmDirectory(absoluteFile.toPath().normalize())
