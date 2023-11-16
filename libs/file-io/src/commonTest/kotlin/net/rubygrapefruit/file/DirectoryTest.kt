package net.rubygrapefruit.file

import kotlin.test.*

class DirectoryTest : AbstractFileSystemElementTest() {
    @Test
    fun `can query path elements`() {
        val dir = fixture.dir("file")
        assertEquals("file", dir.name)
        assertEquals(dir.name, dir.path.name)
        assertEquals(dir.absolutePath, dir.path.absolutePath)
        assertEquals(dir.absolutePath, dir.path.toString())
    }

    @Test
    fun `can query directory metadata`() {
        val dir = fixture.dir("file")
        val metadata = dir.metadata().get()
        assertEquals(DirectoryMetadata, metadata)
    }

    @Test
    fun `can query directory snapshot`() {
        val dir = fixture.dir("file")
        val snapshot = dir.snapshot().get()
        assertEquals(DirectoryMetadata, snapshot.metadata)
        assertEquals(dir.absolutePath, snapshot.absolutePath)
    }

    @Test
    fun `can query metadata of missing directory`() {
        val dir = fixture.testDir.dir("missing")
        val result = dir.metadata()
        assertIs<MissingEntry<*>>(result)
    }

    @Test
    fun `can query snapshot of missing file`() {
        val dir = fixture.testDir.dir("missing")
        val result = dir.snapshot()
        assertIs<MissingEntry<*>>(result)
    }

    @Test
    fun `can create temporary directory`() {
        val dir = fixture.dir("parent")
        val d1 = dir.createTemporaryDirectory()
        val d2 = dir.createTemporaryDirectory()

        assertEquals(dir.absolutePath, d1.parent?.absolutePath)
        assertTrue(d1.metadata().directory)

        assertEquals(dir.absolutePath, d2.parent?.absolutePath)
        assertTrue(d2.metadata().directory)

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
        assertEquals(listOf("dir1", "file1", "link1"), sorted.map { it.path.name })
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

    @Test
    fun `can set and query directory posix permissions`() {
        val dir = fixture.dir("dir")
        assertNotEquals(PosixPermissions.readOnlyDirectory, dir.posixPermissions().get())

        dir.setPermissions(PosixPermissions.readOnlyDirectory)
        val perms = dir.posixPermissions()
        assertEquals(PosixPermissions.readOnlyDirectory, perms.get())
    }
}