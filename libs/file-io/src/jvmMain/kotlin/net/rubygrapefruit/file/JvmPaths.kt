package net.rubygrapefruit.file

import java.nio.file.Path

/**
 * Converts this JVM [Path] to an [ElementPath]
 */
fun Path.resolve(): ElementPath = JvmElementPath(toAbsolutePath().normalize())

/**
 * Converts this JVM [Path] to a [Directory]
 */
fun Path.toDir(): Directory = JvmDirectory(toAbsolutePath().normalize())
