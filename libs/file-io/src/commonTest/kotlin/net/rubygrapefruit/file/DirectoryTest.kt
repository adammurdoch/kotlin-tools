package net.rubygrapefruit.file

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.*

class DirectoryTest {
    private val fixture = FilesFixture()

    @Test
    fun `can create temporary directory`() {
        val dir = fixture.dir("parent")
        val d1 = dir.createTemporaryDirectory()
        val d2 = dir.createTemporaryDirectory()

        assertEquals(dir.absolutePath, d1.parent?.absolutePath)
        assertEquals(DirectoryMetadata, d1.metadata().get())

        assertEquals(dir.absolutePath, d2.parent?.absolutePath)
        assertEquals(DirectoryMetadata, d2.metadata().get())

        assertNotEquals(d1, d2)
        assertNotEquals(d1.name, d2.name)
    }

    @Test
    fun `can create directory`() {
        val parent = fixture.dir("parent")
        val dir = parent.dir("dir")

        assertIs<MissingEntry<*>>(dir.metadata())
        val entries1 = parent.listEntries()
        assertIs<Success<*>>(entries1)
        assertTrue(entries1.get().isEmpty())

        dir.createDirectories()

        assertEquals(DirectoryMetadata, dir.metadata().get())
        val entries2 = dir.listEntries()
        assertIs<Success<*>>(entries2)
        assertTrue(entries2.get().isEmpty())
        val entries3 = parent.listEntries()
        assertIs<Success<*>>(entries3)
        assertEquals(listOf("dir"), entries3.get().map { it.name })
    }

    @Test
    fun `can list contents of directory`() {
        val empty = fixture.dir("empty")
        val dir = fixture.dir("dir") {
            file("file1")
            dir("dir1")
            symLink("link1", "nothing")
        }

        val entries1 = empty.listEntries()
        assertIs<Success<*>>(entries1)
        assertTrue(entries1.get().isEmpty())

        val entries2 = dir.listEntries()
        assertIs<Success<*>>(entries2)
        val sorted = entries2.get().sortedBy { it.name }
        assertEquals(listOf("dir1", "file1", "link1"), sorted.map { it.name })
        assertEquals(listOf(ElementType.Directory, ElementType.RegularFile, ElementType.SymLink), sorted.map { it.type })
    }

    @Test
    fun `cannot list contents of a directory that does not exist`() {
        val dir = fixture.testDir.dir("dir1")

        val entries = dir.listEntries()
        assertIs<MissingEntry<*>>(entries)
    }

    @Test
    fun `cannot list contents of a directory whose parent does not exist`() {
        val parent = fixture.testDir.dir("dir")
        val dir = parent.dir("dir1")

        val entries = dir.listEntries()
        assertIs<MissingEntry<*>>(entries)
    }

    @Test
    fun `cannot list contents of a directory whose ancestor does not exist`() {
        val ancestor = fixture.testDir.dir("dir")
        val dir = ancestor.dir("dir1/dir2")

        val entries = dir.listEntries()
        assertIs<MissingEntry<*>>(entries)
    }

    @Test
    fun `can create intermediate directories`() {
        val dir = fixture.testDir.dir("dir1/dir2/dir3")

        assertIs<MissingEntry<*>>(dir.metadata())
        assertIs<MissingEntry<*>>(dir.parent?.metadata())

        dir.createDirectories()

        assertEquals(DirectoryMetadata, dir.metadata().get())
        assertEquals(DirectoryMetadata, dir.parent?.metadata()?.get())
    }

    @Test
    fun `can create directory that already exists`() {
        val dir = fixture.testDir.dir("dir")
        dir.createDirectories()
        assertEquals(DirectoryMetadata, dir.metadata().get())

        dir.createDirectories()

        assertEquals(DirectoryMetadata, dir.metadata().get())
    }

    @Test
    fun `cannot create directory that exists as a file`() {
        fixture.file("dir")
        val dir = fixture.testDir.dir("dir")

        assertIs<RegularFileMetadata>(dir.metadata().get())

        try {
            dir.createDirectories()
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not create directory $dir as it already exists but is not a directory.", e.message)
        }

        assertIs<RegularFileMetadata>(dir.metadata().get())
    }

    @Test
    fun `cannot create directory whose parent exists as a file`() {
        val file = fixture.file("dir")
        val dir = fixture.testDir.dir("dir/dir2")

        assertIs<RegularFileMetadata>(file.metadata().get())
        assertIs<MissingEntry<*>>(dir.metadata())

        try {
            dir.createDirectories()
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not create directory $file as it already exists but is not a directory.", e.message)
        }

        assertIs<RegularFileMetadata>(file.metadata().get())
        assertIs<MissingEntry<*>>(dir.metadata())
    }

    @Test
    fun `cannot create directory whose ancestor exists as a file`() {
        val file = fixture.file("dir")
        val parent = fixture.testDir.dir("dir/dir2")
        val dir = parent.dir("dir3")

        assertIs<RegularFileMetadata>(file.metadata().get())
        assertIs<MissingEntry<*>>(parent.metadata())
        assertIs<MissingEntry<*>>(dir.metadata())

        try {
            dir.createDirectories()
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not create directory $file as it already exists but is not a directory.", e.message)
        }

        assertIs<RegularFileMetadata>(file.metadata().get())
        assertIs<MissingEntry<*>>(parent.metadata())
        assertIs<MissingEntry<*>>(dir.metadata())
    }
}