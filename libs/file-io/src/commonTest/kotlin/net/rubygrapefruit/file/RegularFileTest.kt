package net.rubygrapefruit.file

import kotlin.test.*

class RegularFileTest : AbstractFileSystemElementTest() {
    @Test
    fun `can query path elements`() {
        val file = fixture.file("file")
        assertEquals("file", file.name)
        assertEquals(file.name, file.path.name)
        assertEquals(file.absolutePath, file.path.absolutePath)
        assertEquals(file.absolutePath, file.path.toString())
    }

    @Test
    fun `can query file metadata`() {
        val file = fixture.file("file", "test")

        val result = file.metadata()
        assertTrue(result.regularFile)

        val metadata = result.get()
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(4, metadata.size)
        assertNotNull(metadata.posixPermissions)
    }

    @Test
    fun `can query file snapshot`() {
        val file = fixture.file("file", "test")
        val snapshot = file.snapshot().get()
        assertIsFileSnapshot(snapshot, file)

        val snapshot2 = file.path.snapshot().get()
        assertIsFileSnapshot(snapshot2, file)

        val snapshot3 = snapshot.snapshot().get()
        assertSame(snapshot, snapshot3)

        val snapshot4 = fixture.testDir.resolve(file.name).snapshot().get()
        assertIsFileSnapshot(snapshot4, file)
    }

    private fun assertIsFileSnapshot(snapshot: ElementSnapshot, file: RegularFile) {
        val metadata = snapshot.metadata
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(4, metadata.size)
        assertEquals(file.absolutePath, snapshot.absolutePath)
    }

    @Test
    fun `can query metadata of missing file`() {
        val file = fixture.testDir.file("missing")

        val result = file.metadata()
        assertIs<MissingEntry<*>>(result)
        assertTrue(result.missing)
    }

    @Test
    fun `can query snapshot of missing file`() {
        val file = fixture.testDir.file("missing")
        val result = file.snapshot()
        assertIs<MissingEntry<*>>(result)

        val result2 = file.path.snapshot()
        assertIs<MissingEntry<*>>(result2)

        val result3 = fixture.testDir.resolve(file.name).snapshot()
        assertIs<MissingEntry<*>>(result3)
    }

    @Test
    fun `can write bytes to a file to create it`() {
        val bytes = "123".encodeToByteArray()
        val file = fixture.testDir.file("file")

        assertIs<MissingEntry<*>>(file.metadata())

        file.writeBytes(bytes)

        val metadata = file.metadata().get()
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(bytes.size.toLong(), metadata.size)

        assertContentEquals(bytes, file.readBytes().get())
    }

    @Test
    fun `can write text to a file to create it`() {
        listOf("1234", "日本語").forEachIndexed { index, text ->
            val file = fixture.testDir.file("file-$index")

            assertIs<MissingEntry<*>>(file.metadata())

            file.writeText(text)

            val metadata = file.metadata().get()
            assertIs<RegularFileMetadata>(metadata)
            assertEquals(text.encodeToByteArray().size.toLong(), metadata.size)

            assertEquals(text, file.readText().get())
        }
    }

    @Test
    fun `can write to file with unicode name`() {
        val file = fixture.testDir.file("日本語")
        file.writeText("1234")

        assertIs<RegularFileMetadata>(file.metadata().get())
        assertEquals("1234", file.readText().get())
    }

    @Test
    fun `can write text to an existing file`() {
        val file = fixture.testDir.file("file")
        file.writeText("1234")

        assertIs<RegularFileMetadata>(file.metadata().get())

        file.writeText("12345678")

        val metadata = file.metadata().get()
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(8, metadata.size)
    }

    @Test
    fun `cannot write to a file that is a directory`() {
        fixture.dir("dir1")
        val file = fixture.testDir.file("dir1")

        try {
            file.writeText("broken")
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not write to $file as it already exists but is not a file.", e.message)
        }
    }

    @Test
    fun `cannot write to a file whose parent does not exist`() {
        val parent = fixture.testDir.dir("dir1")
        val file = parent.file("file.txt")

        try {
            file.writeText("broken")
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not write to $file as directory $parent does not exist.", e.message)
        }
    }

    @Test
    fun `cannot write to a file whose ancestor does not exist`() {
        val ancestor = fixture.testDir.dir("dir1")
        val file = ancestor.file("dir2/file.txt")

        try {
            file.writeText("broken")
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not write to $file as directory $ancestor does not exist.", e.message)
        }
    }

    @Test
    fun `cannot write to a file whose parent is a file`() {
        val parent = fixture.file("dir1")
        val file = fixture.testDir.file("dir1/file.txt")

        try {
            file.writeText("broken")
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not write to $file as $parent exists but is not a directory.", e.message)
        }
    }

    @Test
    fun `cannot write to a file whose ancestor is a file`() {
        val ancestor = fixture.file("dir1")
        val file = fixture.testDir.file("dir1/dir2/file.txt")

        try {
            file.writeText("broken")
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not write to $file as $ancestor exists but is not a directory.", e.message)
        }
    }

    @Test
    fun `can set and query file posix permissions`() {
        val file = fixture.file("file")
        assertTrue(file.supports(FileSystemCapability.PosixPermissions))
        assertNotEquals(PosixPermissions.readOnlyFile, file.posixPermissions().get())

        file.setPermissions(PosixPermissions.readOnlyFile)
        assertEquals(PosixPermissions.readOnlyFile, file.posixPermissions().get())
    }

    @Test
    fun `can make file executable`() {
        val file = fixture.file("file")
        assertFalse(file.posixPermissions().get().isOwnerExecutable)

        file.setPermissions(file.posixPermissions().get().executable())
        assertTrue(file.posixPermissions().get().isOwnerExecutable)
    }
}