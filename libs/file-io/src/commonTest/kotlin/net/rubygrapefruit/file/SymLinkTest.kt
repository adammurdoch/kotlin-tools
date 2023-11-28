package net.rubygrapefruit.file

import kotlin.test.*

class SymLinkTest : AbstractFileSystemElementTest<SymLink>() {

    override fun create(name: String): SymLink {
        return fixture.symlink(name, "target")
    }

    override fun missing(name: String): SymLink {
        return fixture.testDir.symLink(name)
    }

    override fun canSetPermissions(element: SymLink): Boolean {
        return element.supports(FileSystemCapability.SetSymLinkPosixPermissions)
    }

    @Test
    fun `can query symlink metadata`() {
        val link = fixture.symlink("file", "test")

        val result = link.metadata()
        assertTrue(result.symlink)

        val metadata = result.get()
        assertIs<SymlinkMetadata>(metadata)
        assertEquals(link.supports(FileSystemCapability.PosixPermissions), metadata.posixPermissions != null)
    }

    @Test
    fun `can query symlink snapshot`() {
        val link = fixture.symlink("file", "test")

        val snapshot = link.snapshot().get()
        assertIs<SymlinkMetadata>(snapshot.metadata)

        val snapshot2 = link.path.snapshot().get()
        assertIs<SymlinkMetadata>(snapshot2.metadata)
    }

    @Test
    fun `can query metadata of missing symlink`() {
        val link = fixture.testDir.symLink("missing")
        val result = link.metadata()
        assertIs<MissingEntry<*>>(result)
        assertTrue(result.missing)
    }

    @Test
    fun `can query snapshot of missing symlink`() {
        val link = fixture.testDir.symLink("missing")

        val result = link.snapshot()
        assertIs<MissingEntry<*>>(result)

        val result2 = link.path.snapshot()
        assertIs<MissingEntry<*>>(result2)
    }

    @Test
    fun `can create a symlink`() {
        val link = fixture.testDir.symLink("link")

        assertTrue(link.metadata().missing)

        link.writeSymLink("1234")

        assertIs<SymlinkMetadata>(link.metadata().get())
        assertEquals("1234", link.readSymLink().get())
    }

    @Test
    fun `can overwrite a symlink`() {
        val link = fixture.testDir.symLink("link")
        link.writeSymLink("1234")

        link.writeSymLink("abc")

        assertIs<SymlinkMetadata>(link.metadata().get())
        assertEquals("abc", link.readSymLink().get())
    }

    @Test
    fun `can create a symlink that references a file`() {
        val file = fixture.file("other.txt")
        file.writeText("1234")

        val link = fixture.testDir.symLink("link")

        link.writeSymLink(file.name)

        assertEquals("1234", fixture.testDir.file("link").readText().get())
    }

    @Test
    fun `can set and query symlink posix permissions`() {
        val file = fixture.file("file")
        val symLink = fixture.symlink("link", file.name)
        assertTrue(symLink.supports(FileSystemCapability.PosixPermissions))
        assertNotEquals(PosixPermissions.readOnlyFile, symLink.posixPermissions().get())
        assertNotEquals(PosixPermissions.readWriteFile, symLink.posixPermissions().get())

        file.setPermissions(PosixPermissions.readOnlyFile)

        assertNotEquals(file.posixPermissions().get(), symLink.posixPermissions().get())

        if (!symLink.supports(FileSystemCapability.SetSymLinkPosixPermissions)) {
            return
        }
        symLink.setPermissions(PosixPermissions.readWriteFile)

        assertEquals(PosixPermissions.readWriteFile, symLink.posixPermissions().get())
    }

    @Test
    fun `cannot set symlink posix permissions when not supported`() {
        val file = fixture.file("file")
        val symLink = fixture.symlink("link", file.name)
        assertTrue(symLink.supports(FileSystemCapability.PosixPermissions))

        if (symLink.supports(FileSystemCapability.SetSymLinkPosixPermissions)) {
            return
        }

        try {
            symLink.setPermissions(PosixPermissions.readWriteFile)
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not set POSIX permissions on $symLink as it is not supported by this filesystem.", e.message)
        }
    }
}