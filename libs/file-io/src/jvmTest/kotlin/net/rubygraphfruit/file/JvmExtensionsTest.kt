package net.rubygraphfruit.file

import net.rubygrapefruit.file.*
import java.io.File
import kotlin.io.path.pathString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JvmExtensionsTest : AbstractFileSystemElementTest() {
    @Test
    fun `can convert RegularFile to absolute Java File`() {
        val element = fixture.file("file.txt")
        val file = element.toFile()
        assertEquals(element.absolutePath, file.absolutePath)
        assertTrue(file.isAbsolute)
    }

    @Test
    fun `can convert RegularFile to absolute Java Path`() {
        val element = fixture.file("file.txt")
        val path = element.toPath()
        assertEquals(element.absolutePath, path.pathString)
        assertTrue(path.isAbsolute)
    }

    @Test
    fun `can convert relative Java File to ElementPath`() {
        val element = fileSystem.currentDirectory.file("file.txt")
        val file = File("file.txt")
        val filePath = file.resolve()
        assertEquals(element.absolutePath, filePath.absolutePath)
    }

    @Test
    fun `can convert absolute Java File to ElementPath`() {
        val element = fileSystem.currentDirectory.file("file.txt")
        val file = File("file.txt").absoluteFile
        val filePath = file.resolve()
        assertEquals(element.absolutePath, filePath.absolutePath)
    }

    @Test
    fun `can convert relative Java File to Directory`() {
        val element = fileSystem.currentDirectory.dir("dir")
        val file = File("dir")
        val dir = file.toDir()
        assertEquals(element.absolutePath, dir.absolutePath)
    }

    @Test
    fun `can convert relative Java Path to ElementPath`() {
        val element = fileSystem.currentDirectory.file("file.txt")
        val path = File("file.txt").toPath()
        val filePath = path.resolve()
        assertEquals(element.absolutePath, filePath.absolutePath)
    }

    @Test
    fun `can convert relative Java Path to Directory`() {
        val element = fileSystem.currentDirectory.dir("dir")
        val path = File("dir").toPath()
        val dir = path.toDir()
        assertEquals(element.absolutePath, dir.absolutePath)
    }
}