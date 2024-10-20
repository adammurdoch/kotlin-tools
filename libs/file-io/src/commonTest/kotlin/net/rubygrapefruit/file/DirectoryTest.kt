package net.rubygrapefruit.file

import kotlin.test.*

class DirectoryTest : AbstractFileSystemElementTest<Directory>() {

    override fun create(name: String): Directory {
        return fixture.dir(name) {
            file("not-empty")
        }
    }

    override fun missing(name: String): Directory {
        return fixture.testDir.dir(name)
    }

    @Test
    fun `can query directory metadata`() {
        val dir = fixture.dir("dir")

        val result = dir.metadata()
        assertTrue(result.directory)

        val metadata = result.get()
        assertIs<DirectoryMetadata>(metadata)
        assertEquals(dir.supports(FileSystemCapability.PosixPermissions), metadata.posixPermissions != null)
    }

    @Test
    fun `can resolve files relative to directory`() {
        val dir = fixture.dir("dir")

        val child = dir.path.resolve("child")
        val sub = dir.path.resolve("child/sub")
        val parent = dir.parent!!.path

        val values = mapOf<String, ElementPath>(
            "child" to child,
            "child/sub" to sub,
            "." to dir.path,
            "./." to dir.path,
            "" to dir.path,
            "./child" to child,
            "child/." to child,
            ".." to parent,
            "./../." to parent,
            "../dir/./child" to child,
            "../sibling" to parent.resolve("sibling"),
            "./../x/../." to parent,
            dir.absolutePath to dir.path,
            child.absolutePath to child,
            parent.absolutePath to parent,
        )

        for (entry in values) {
            val file = dir.file(entry.key)
            assertEquals(entry.value, file.path)
        }
    }

    @Test
    fun `can query directory snapshot`() {
        val dir = fixture.dir("file")
        val snapshot = dir.snapshot().get()
        assertIsDirectorySnapshot(snapshot, dir)

        val snapshot2 = dir.path.snapshot().get()
        assertIsDirectorySnapshot(snapshot2, dir)

        val snapshot3 = fixture.testDir.path.resolve(dir.name).snapshot().get()
        assertIsDirectorySnapshot(snapshot3, dir)
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
        assertTrue(entries1.isEmpty())

        dir.createDirectories()

        assertIs<DirectoryMetadata>(dir.metadata().get())
        val entries2 = dir.listEntries()
        assertTrue(entries2.isEmpty())
        val entries3 = parent.listEntries()
        assertEquals(listOf("dir"), entries3.map { it.name })
    }

    @Test
    fun `can create intermediate directories`() {
        val dir = fixture.testDir.dir("dir1/dir2/dir3")

        assertIs<MissingEntry<*>>(dir.metadata())
        assertIs<MissingEntry<*>>(dir.parent?.metadata())

        dir.createDirectories()

        assertIs<DirectoryMetadata>(dir.metadata().get())
        assertIs<DirectoryMetadata>(dir.parent?.metadata()?.get())
    }

    @Test
    fun `can create directory that already exists`() {
        val dir = fixture.testDir.dir("dir")
        dir.createDirectories()
        assertIs<DirectoryMetadata>(dir.metadata().get())

        dir.createDirectories()

        assertIs<DirectoryMetadata>(dir.metadata().get())
    }

    @Test
    fun `cannot create directory that exists as a file`() {
        fixture.file("dir")
        val dir = fixture.testDir.dir("dir")

        assertIs<RegularFileMetadata>(dir.metadata().get())

        failsWith<FileSystemException>("Could not create directory $dir as it already exists but is not a directory.") {
            dir.createDirectories()
        }

        assertIs<RegularFileMetadata>(dir.metadata().get())
    }

    @Test
    fun `cannot create directory whose parent exists as a file`() {
        val file = fixture.file("dir")
        val dir = fixture.testDir.dir("dir/dir2")

        assertIs<RegularFileMetadata>(file.metadata().get())
        assertIs<MissingEntry<*>>(dir.metadata())

        failsWith<FileSystemException>("Could not create directory $file as it already exists but is not a directory.") {
            dir.createDirectories()
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

        failsWith<FileSystemException>("Could not create directory $file as it already exists but is not a directory.") {
            dir.createDirectories()
        }

        assertIs<RegularFileMetadata>(file.metadata().get())
        assertIs<MissingEntry<*>>(parent.metadata())
        assertIs<MissingEntry<*>>(dir.metadata())
    }

    @Test
    fun `can list contents of directory`() {
        val empty = fixture.dir("empty")
        val dir = fixture.dir("dir") {
            file("file1")
            dir("dir1") {
                file("nested")
            }
            symLink("link1", "nothing")
            symLink("link2", "dir1")
        }

        val entries1 = empty.listEntries()
        assertTrue(entries1.isEmpty())

        val entries2 = dir.listEntries()
        val sorted = entries2.sortedBy { it.name }
        assertEquals(listOf("dir1", "file1", "link1", "link2"), sorted.map { it.name })
        assertEquals(listOf("dir1", "file1", "link1", "link2"), sorted.map { it.path.name })
        assertEquals(listOf(ElementType.Directory, ElementType.RegularFile, ElementType.SymLink, ElementType.SymLink), sorted.map { it.type })
        assertTrue(entries2.all { it.path.parent == dir.path })
    }

    @Test
    fun `cannot list contents of a directory that does not exist`() {
        val dir = fixture.testDir.dir("dir1")

        failsWith<MissingDirectoryException>("Could not list directory $dir as it does not exist.") {
            dir.listEntries()
        }
    }

    @Test
    fun `cannot list contents of a directory whose parent does not exist`() {
        val parent = fixture.testDir.dir("dir")
        val dir = parent.dir("dir1")

        failsWith<MissingDirectoryException>("Could not list directory $dir as it does not exist.") {
            dir.listEntries()
        }
    }

    @Test
    fun `cannot list contents of a directory whose ancestor does not exist`() {
        val ancestor = fixture.testDir.dir("dir")
        val dir = ancestor.dir("dir1/dir2")

        failsWith<MissingDirectoryException>("Could not list directory $dir as it does not exist.") {
            dir.listEntries()
        }
    }

    @Test
    fun `cannot list contents of a directory that exists as a file`() {
        val dir = fixture.file("file").toDir()

        failsWith<FileSystemException>("Could not list directory $dir as it is not a directory.") {
            dir.listEntries()
        }
    }

    @Test
    fun `cannot list contents of an unreadable directory`() {
        if (!canRecoverFromNoPermissions()) {
            // For some reason cannot restore the directory permissions on the JVM, in order to delete the directory later
            return
        }

        val dir = fixture.dir("dir") {
            file("file1")
        }

        if (!canSetPermissions(dir)) {
            return
        }
        dir.setPermissions(PosixPermissions.nothing)

        failsWith<DirectoryPermissionException>("Directory $dir is not readable.") {
            dir.listEntries()
        }
    }

    @Test
    fun `can visit contents of directory in top down order`() {
        val empty = fixture.dir("empty")
        val dir = fixture.dir("dir") {
            file("file1")
            dir("dir1") {
                file("nested")
                dir("dir2")
            }
            symLink("link1", "nothing")
            symLink("link2", "dir1")
        }

        val visited1 = mutableListOf<String>()
        empty.visitTopDown { visited1.add(name) }
        assertEquals(listOf("empty"), visited1)

        val visited2 = mutableListOf<String>()
        dir.visitTopDown { visited2.add(name) }
        assertEquals("dir", visited2[0])
        assertEquals(listOf("dir", "dir1", "dir2", "file1", "link1", "link2", "nested"), visited2.sorted())
        assertTrue(visited2.indexOf("dir1") < visited2.indexOf("nested"))
    }

    @Test
    fun `can delete an empty directory`() {
        val dir = fixture.dir("empty")
        assertTrue(dir.metadata().directory)

        dir.deleteRecursively()

        assertTrue(dir.metadata().missing)
    }

    @Test
    fun `can delete a non-empty directory`() {
        val dir = fixture.dir("empty") {
            dir("sub1") {
                dir("sub2") {
                    file("file.txt")
                }
                file("file.txt")
            }
            file("file.txt")
        }
        assertTrue(dir.metadata().directory)

        dir.deleteRecursively()

        assertTrue(dir.metadata().missing)
    }

    @Test
    fun `can delete a directory that does not exist`() {
        val dir = fixture.testDir.dir("missing")
        assertTrue(dir.metadata().missing)

        dir.deleteRecursively()

        assertTrue(dir.metadata().missing)
    }

    @Test
    fun `delete does not follow symlinks`() {
        val file = fixture.file("target")
        file.writeText("content")

        val dir = fixture.dir("dir") {
            symLink("link", file.absolutePath)
        }

        assertTrue(file.metadata().regularFile)

        dir.deleteRecursively()

        assertTrue(dir.metadata().missing)
        assertTrue(file.metadata().regularFile)
    }

    @Test
    fun `cannot delete a directory that exists as a file`() {
        val dir = fixture.file("file").toDir()
        assertTrue(dir.metadata().regularFile)

        failsWith<FileSystemException>("Could not delete directory $dir as it is not a directory.") {
            dir.deleteRecursively()
        }

        assertTrue(dir.metadata().regularFile)
    }

    @Test
    fun `cannot delete a read only directory`() {
        val dir = fixture.dir("dir") {
            file("file1")
        }
        val file = dir.file("file1")

        if (!canSetPermissions(dir)) {
            return
        }
        dir.setPermissions(PosixPermissions.readOnlyDirectory)

        failsWith<FileSystemException>("Could not delete file $file as directory $dir is not writable.") {
            dir.deleteRecursively()
        }
    }

    private fun assertIsDirectorySnapshot(snapshot: ElementSnapshot, dir: Directory) {
        assertIs<DirectoryMetadata>(snapshot.metadata)
        assertEquals(dir.absolutePath, snapshot.absolutePath)
    }
}