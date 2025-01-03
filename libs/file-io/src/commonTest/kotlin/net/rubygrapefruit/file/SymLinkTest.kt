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
    fun `can create a symlink that references nothing`() {
        val link = fixture.testDir.symLink("link")

        assertTrue(link.metadata().missing)

        link.writeSymLink("1234")

        assertIs<SymlinkMetadata>(link.metadata().get())
        assertEquals("1234", link.readSymLink())
    }

    @Test
    fun `can overwrite a symlink`() {
        val link = fixture.testDir.symLink("link")
        link.writeSymLink("1234")

        link.writeSymLink("abc")

        assertIs<SymlinkMetadata>(link.metadata().get())
        assertEquals("abc", link.readSymLink())
    }

    @Test
    fun `can create a symlink that references a file`() {
        val file = fixture.file("other.txt")
        file.writeText("1234")

        val link = fixture.testDir.symLink("link")

        link.writeSymLink(file.name)

        assertIs<SymlinkMetadata>(link.metadata().get())
        assertEquals(file.name, link.readSymLink())
    }

    @Test
    fun `can create a symlink that references a directory`() {
        val dir = fixture.dir("dir") {
            file("a1.txt")
        }

        val link = fixture.testDir.symLink("link")

        link.writeSymLink(dir.name)

        assertIs<SymlinkMetadata>(link.metadata().get())
        assertEquals(dir.name, link.readSymLink())
    }

    @Test
    fun `can resolve relative symlink to file`() {
        val target = fixture.file("other.txt")
        target.writeText("1234")

        val link = fixture.testDir.symLink("link")

        link.writeSymLink(target.name)

        val result = link.resolve()

        assertIs<Success<*>>(result)
        assertIs<RegularFileMetadata>(result.get().metadata)
        assertEquals(target.path, result.get().path)
    }

    @Test
    fun `can resolve relative symlink to file in ancestor directory`() {
        val target = fixture.file("file.txt")
        val dir = fixture.dir("dir") {
            symLink("link", "../file.txt")
        }

        val link = dir.symLink("link")

        val result = link.resolve()

        assertIs<Success<*>>(result)
        assertIs<RegularFileMetadata>(result.get().metadata)
        assertEquals(target.path, result.get().path)
    }

    @Test
    fun `can resolve absolute symlink to file`() {
        val target = fixture.file("other.txt")
        target.writeText("1234")

        val link = fixture.testDir.symLink("link")

        link.writeSymLink(target.absolutePath)

        val result = link.resolve()

        assertIs<Success<*>>(result)
        assertIs<RegularFileMetadata>(result.get().metadata)
        assertEquals(target.path, result.get().path)
    }

    @Test
    fun `can resolve a chain of symlinks to file`() {
        val target = fixture.file("other.txt")
        target.writeText("1234")

        val link1 = fixture.testDir.symLink("link-1")
        link1.writeSymLink(target.name)

        val link2 = fixture.testDir.symLink("link-2")
        link2.writeSymLink(link1.name)

        val result = link2.resolve()

        assertIs<Success<*>>(result)
        assertIs<RegularFileMetadata>(result.get().metadata)
        assertEquals(target.path, result.get().path)
    }

    @Test
    fun `can resolve a chain of relative symlinks to files in ancestor directory`() {
        val target = fixture.file("file.txt")
        val dir = fixture.dir("dir") {
            symLink("file.txt", "../file.txt")
            dir("dir1") {
                symLink("file.txt", "../file.txt")
            }
        }

        val link = dir.symLink("dir1/file.txt")

        val result = link.resolve()

        assertIs<Success<*>>(result)
        assertIs<RegularFileMetadata>(result.get().metadata)
        assertEquals(target.path, result.get().path)
    }

    @Test
    fun `can resolve relative symlink to directory`() {
        val target = fixture.dir("dir") {
            file("file.txt")
        }

        val link = fixture.testDir.symLink("link")

        link.writeSymLink(target.name)

        val result = link.resolve()

        assertIs<Success<*>>(result)
        assertIs<DirectoryMetadata>(result.get().metadata)
        assertEquals(target.path, result.get().path)
    }

    @Test
    fun `can resolve relative symlink to missing element`() {
        val link = fixture.testDir.symLink("link")

        link.writeSymLink("unknown")

        val result = link.resolve()

        assertIs<MissingEntry<*>>(result)
    }

    @Test
    fun `set and query posix permissions on symlink acts on the link and not the target`() {
        val file = fixture.file("file")
        val symLink = fixture.symlink("link", file.name)

        if (!symLink.supports(FileSystemCapability.SetSymLinkPosixPermissions) || !symLink.supports(FileSystemCapability.PosixPermissions)) {
            return
        }

        assertNotEquals(PosixPermissions.readOnlyFile, symLink.posixPermissions())
        assertNotEquals(PosixPermissions.readWriteFile, symLink.posixPermissions())

        file.setPermissions(PosixPermissions.readOnlyFile)

        assertNotEquals(file.posixPermissions(), symLink.posixPermissions())

        if (!symLink.supports(FileSystemCapability.SetSymLinkPosixPermissions)) {
            return
        }
        symLink.setPermissions(PosixPermissions.readWriteFile)

        assertEquals(PosixPermissions.readWriteFile, symLink.posixPermissions())
    }

    @Test
    fun `cannot read symlink that does not exist`() {
        val parent = fixture.testDir.dir("dir")
        val link = parent.symLink("file.txt")

        failsWith<FileSystemException>("Could not read symlink $link as it does not exist.") {
            link.readSymLink()
        }
    }

    @Test
    fun `cannot read unreadable symlink`() {
        val file = fixture.file("file.txt")
        val link = fixture.symlink("link.txt", file.absolutePath)
        if (!link.supports(FileSystemCapability.SetSymLinkPosixPermissions) || !link.supports(FileSystemCapability.PosixPermissions)) {
            return
        }
        link.setPermissions(link.posixPermissions().writeOnly())

        failsWith<SymlinkPermissionException>("Symlink $link is not readable.") {
            link.readSymLink()
        }
    }
}