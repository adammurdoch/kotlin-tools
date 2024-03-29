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

        assertIs<SymlinkMetadata>(link.metadata().get())
        assertEquals(file.name, link.readSymLink().get())
    }

    @Test
    fun `set and query posix permissions on symlink acts on the link and not the target`() {
        val file = fixture.file("file")
        val symLink = fixture.symlink("link", file.name)

        if (!symLink.supports(FileSystemCapability.SetSymLinkPosixPermissions) || !symLink.supports(FileSystemCapability.PosixPermissions)) {
            return
        }

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
}