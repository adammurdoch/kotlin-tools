package net.rubygrapefruit.file

import kotlinx.io.files.Path
import net.rubygrapefruit.file.fixtures.AbstractFileTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PathExtensionsTest : AbstractFileTest() {
    @Test
    fun `can convert relative Path to ElementPath`() {
        val element = fileSystem.currentDirectory.file("file.txt")
        val path = Path("file.txt")
        val filePath = path.resolve()
        assertEquals(element.absolutePath, filePath.absolutePath)
    }

    @Test
    fun `can convert absolute Path to ElementPath`() {
        val element = fileSystem.currentDirectory.file("file.txt")
        val path = Path(element.absolutePath)
        val filePath = path.resolve()
        assertEquals(element.absolutePath, filePath.absolutePath)
    }

    @Test
    fun `can convert relative Path to Directory`() {
        val element = fileSystem.currentDirectory.dir("dir")
        val path = Path("dir")
        val dir = path.toDir()
        assertEquals(element.absolutePath, dir.absolutePath)
    }

    @Test
    fun `can convert absolute Path to Directory`() {
        val element = fileSystem.currentDirectory.dir("dir")
        val path = Path(element.absolutePath)
        val dir = path.toDir()
        assertEquals(element.absolutePath, dir.absolutePath)
    }

    @Test
    fun `can convert relative Path to RegularFile`() {
        val element = fileSystem.currentDirectory.file("file.txt")
        val path = Path("file.txt")
        val file = path.toFile()
        assertEquals(element.absolutePath, file.absolutePath)
    }

    @Test
    fun `can convert relative Path to Symlink`() {
        val element = fileSystem.currentDirectory.file("file.txt")
        val path = Path("file.txt")
        val file = path.toSymlink()
        assertEquals(element.absolutePath, file.absolutePath)
    }
}