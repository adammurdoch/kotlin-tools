package net.rubygrapefruit.file

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class SymLinkTest : AbstractFileSystemElementTest() {
    @Test
    fun `can create a symlink`() {
        val link = fixture.testDir.symLink("link")

        assertIs<MissingEntry<*>>(link.metadata())

        link.writeSymLink("1234")

        assertEquals(SymlinkMetadata, link.metadata().get())
        assertEquals("1234", link.readSymLink().get())
    }

    @Test
    fun `can overwrite a symlink`() {
        val link = fixture.testDir.symLink("link")
        link.writeSymLink("1234")

        link.writeSymLink("abc")

        assertEquals(SymlinkMetadata, link.metadata().get())
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
        if (!fixture.testDir.supports(FileSystemCapability.SetSymLinkPosixPermissions)) {
            return
        }

        val file = fixture.file("file")
        val symLink = fixture.symlink("link", file.name)

        assertNotEquals(PosixPermissions.readOnlyFile, symLink.posixPermissions().get())
        assertNotEquals(PosixPermissions.readWriteFile, symLink.posixPermissions().get())

        file.setPermissions(PosixPermissions.readOnlyFile)

        assertNotEquals(file.posixPermissions().get(), symLink.posixPermissions().get())

        symLink.setPermissions(PosixPermissions.readWriteFile)

        assertEquals(PosixPermissions.readWriteFile, symLink.posixPermissions().get())
    }
}