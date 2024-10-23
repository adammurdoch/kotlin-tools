package net.rubygrapefruit.file

import kotlinx.io.files.Path

/**
 * Converts this [Path] to an [ElementPath]
 */
fun Path.resolve(): ElementPath = FileSystem.local.currentDirectory.path.resolve(toString())

/**
 * Converts this [Path] to a [Directory]
 */
fun Path.toDir(): Directory = FileSystem.local.currentDirectory.dir(toString())

/**
 * Converts this [Path] to a [RegularFile]
 */
fun Path.toFile(): RegularFile = FileSystem.local.currentDirectory.file(toString())

/**
 * Converts this [Path] to a [SymLink]
 */
fun Path.toSymlink(): SymLink = FileSystem.local.currentDirectory.symLink(toString())
