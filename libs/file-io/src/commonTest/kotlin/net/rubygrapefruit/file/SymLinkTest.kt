package net.rubygrapefruit.file

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SymLinkTest {
    private val fixture = FilesFixture()

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
}