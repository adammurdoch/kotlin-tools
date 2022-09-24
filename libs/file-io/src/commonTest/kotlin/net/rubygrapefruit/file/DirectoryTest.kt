package net.rubygrapefruit.file

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.*

class DirectoryTest {
    private val fixture = FilesFixture()

    @Test
    fun `can query home and current directories`() {
        assertNotNull(Directory.current)
        assertNotNull(Directory.userHome)
    }

    @Test
    fun `can create temporary directory`() {
        val dir = fixture.dir("parent")
        val d1 = dir.createTemporaryDirectory()
        val d2 = dir.createTemporaryDirectory()

        assertEquals(dir.absolutePath, d1.parent?.absolutePath)
        assertEquals(DirectoryMetadata, d1.metadata())

        assertEquals(dir.absolutePath, d2.parent?.absolutePath)
        assertEquals(DirectoryMetadata, d2.metadata())

        assertNotEquals(d1, d2)
        assertNotEquals(d1.name, d2.name)
    }

    @Test
    fun `can create directory`() {
        val dir = fixture.testDir.dir("dir")

        assertEquals(MissingEntryMetadata, dir.metadata())

        dir.createDirectories()

        assertEquals(DirectoryMetadata, dir.metadata())
    }

    @Test
    fun `can create intermediate directories`() {
        val dir = fixture.testDir.dir("dir1/dir2/dir3")

        assertEquals(MissingEntryMetadata, dir.metadata())
        assertEquals(MissingEntryMetadata, dir.parent?.metadata())

        dir.createDirectories()

        assertEquals(DirectoryMetadata, dir.metadata())
        assertEquals(DirectoryMetadata, dir.parent?.metadata())
    }

    @Test
    fun `can create directory that already exists`() {
        val dir = fixture.testDir.dir("dir")
        dir.createDirectories()
        assertEquals(DirectoryMetadata, dir.metadata())

        dir.createDirectories()

        assertEquals(DirectoryMetadata, dir.metadata())
    }

    @Test
    fun `cannot create directory that exists as a file`() {
        fixture.file("dir")
        val dir = fixture.testDir.dir("dir")

        assertIs<RegularFileMetadata>(dir.metadata())

        try {
            dir.createDirectories()
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not create directory $dir as it already exists but is not a directory.", e.message)
        }

        assertIs<RegularFileMetadata>(dir.metadata())
    }

    @Test
    fun `cannot create directory whose parent exists as a file`() {
        val file = fixture.file("dir")
        val dir = fixture.testDir.dir("dir/dir2")

        assertIs<RegularFileMetadata>(file.metadata())
        assertEquals(MissingEntryMetadata, dir.metadata())

        try {
            dir.createDirectories()
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not create directory $file as it already exists but is not a directory.", e.message)
        }

        assertIs<RegularFileMetadata>(file.metadata())
        assertEquals(MissingEntryMetadata, dir.metadata())
    }

    @Test
    fun `cannot create directory whose ancestor exists as a file`() {
        val file = fixture.file("dir")
        val parent = fixture.testDir.dir("dir/dir2")
        val dir = parent.dir("dir3")

        assertIs<RegularFileMetadata>(file.metadata())
        assertEquals(MissingEntryMetadata, parent.metadata())
        assertEquals(MissingEntryMetadata, dir.metadata())

        try {
            dir.createDirectories()
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not create directory $file as it already exists but is not a directory.", e.message)
        }

        assertIs<RegularFileMetadata>(file.metadata())
        assertEquals(MissingEntryMetadata, parent.metadata())
        assertEquals(MissingEntryMetadata, dir.metadata())
    }
}