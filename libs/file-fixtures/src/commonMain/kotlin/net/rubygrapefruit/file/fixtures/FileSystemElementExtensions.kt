package net.rubygrapefruit.file.fixtures

import net.rubygrapefruit.file.FileSystemElement
import net.rubygrapefruit.file.directory
import net.rubygrapefruit.file.regularFile
import kotlin.test.assertTrue

/**
 * Asserts that the given element exists and is a directory.
 */
fun assertIsDirectory(file: FileSystemElement) = assertTrue(file.metadata().directory)

/**
 * Asserts that the given element exists and is a regular file.
 */
fun assertIsFile(file: FileSystemElement) = assertTrue(file.metadata().regularFile)
