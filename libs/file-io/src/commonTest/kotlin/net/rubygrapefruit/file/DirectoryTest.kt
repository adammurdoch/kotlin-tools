package net.rubygrapefruit.file

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail

class DirectoryTest {
    private val fixture = FilesFixture()

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
            assertEquals("Could not create directory ${dir.parent} as it already exists but is not a directory.", e.message)
        }

        assertIs<RegularFileMetadata>(file.metadata())
        assertEquals(MissingEntryMetadata, dir.metadata())
    }
}