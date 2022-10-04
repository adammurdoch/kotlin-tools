package net.rubygrapefruit.file.fixtures

import net.rubygrapefruit.file.FileSystemElement
import net.rubygrapefruit.file.MissingEntry
import net.rubygrapefruit.file.directory
import net.rubygrapefruit.file.regularFile
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Asserts that the given element exists and is a directory.
 */
fun assertIsDirectory(file: FileSystemElement) = assertTrue(file.metadata().directory)

/**
 * Asserts that the given element exists and is a regular file.
 */
fun assertIsFile(file: FileSystemElement) = assertTrue(file.metadata().regularFile)

/**
 * Asserts that the given element does not exist.
 */
fun assertIsMissing(file: FileSystemElement) = assertIs<MissingEntry<*>>(file.metadata())
