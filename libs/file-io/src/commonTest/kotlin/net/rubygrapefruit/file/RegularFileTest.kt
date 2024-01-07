package net.rubygrapefruit.file

import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.stream.CollectingBuffer
import net.rubygrapefruit.io.stream.EndOfStream
import net.rubygrapefruit.io.stream.ReadBytes
import net.rubygrapefruit.io.stream.ReadFailed
import kotlin.test.*

class RegularFileTest : AbstractFileSystemElementTest<RegularFile>() {

    override fun create(name: String): RegularFile {
        return fixture.file(name)
    }

    override fun missing(name: String): RegularFile {
        return fixture.testDir.file(name)
    }

    private val writeActions: List<(RegularFile) -> Unit> = listOf(
        { file -> file.writeText("text") },
        { file -> file.writeBytes("text".encodeToByteArray()) },
        { file -> file.writeBytes { it.write("text".encodeToByteArray()) } },
        { file -> file.writeBytes { } },
    )

    /**
     * Read actions that attempt to read content from the file
     */
    private val readActionsThatStream: List<(RegularFile) -> Result<*>> = listOf(
        { file -> file.readText() },
        { file -> file.readBytes() },
        { file ->
            file.readBytes { stream ->
                val result = stream.read(ByteArray(1024))
                when (result) {
                    is ReadFailed -> {
                        FailedOperation(result.exception)
                    }

                    EndOfStream, is ReadBytes -> {
                        Success("ok")
                    }
                }
            }
        },
    )

    private val readActions: List<(RegularFile) -> Result<*>> =
        readActionsThatStream + listOf(
            { file -> file.readBytes { Success(it.read(ByteArray(0))) } },
            { file -> file.readBytes { Success(12) } },
        )

    @Test
    fun `can query file metadata`() {
        val file = fixture.file("file", "test")

        val result = file.metadata()
        assertTrue(result.regularFile)

        val metadata = result.get()
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(4, metadata.size)
        assertEquals(file.supports(FileSystemCapability.PosixPermissions), metadata.posixPermissions != null)
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

        val snapshot4 = fixture.testDir.path.resolve(file.name).snapshot().get()
        assertIsFileSnapshot(snapshot4, file)
    }

    private fun assertIsFileSnapshot(snapshot: ElementSnapshot, file: RegularFile) {
        val metadata = snapshot.metadata
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(4, metadata.size)
        assertEquals(file.absolutePath, snapshot.absolutePath)
    }

    @Test
    fun `can stream bytes to a file to create it`() {
        listOf("123".encodeToByteArray(), byteArrayOf()).forEachIndexed { index, bytes ->
            val file = fixture.testDir.file("file-$index")
            assertTrue(file.metadata().missing)

            file.writeBytes { stream ->
                stream.write(bytes)
            }

            val metadata = file.metadata().get()
            assertIs<RegularFileMetadata>(metadata)
            assertEquals(bytes.size.toLong(), metadata.size)

            assertContentEquals(bytes, file.readBytes().get())
        }
    }

    @Test
    fun `can stream nothing to a file to create it`() {
        val file = fixture.testDir.file("file")
        assertTrue(file.metadata().missing)

        file.writeBytes { _ -> }

        val metadata = file.metadata().get()
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(0, metadata.size)

        assertContentEquals(byteArrayOf(), file.readBytes().get())
    }

    @Test
    fun `can random access write bytes to a file to create it`() {
        listOf("123".encodeToByteArray(), byteArrayOf()).forEachIndexed { index, bytes ->
            val file = fixture.testDir.file("file-$index")
            assertTrue(file.metadata().missing)

            val result = file.withContent { content ->
                assertEquals(0L, content.length())
                assertEquals(0L, content.currentPosition)

                content.writeStream.write(bytes)

                assertEquals(bytes.size.toLong(), content.length())
                assertEquals(bytes.size.toLong(), content.currentPosition)

                "result"
            }

            assertIs<Success<*>>(result)
            assertEquals("result", result.get())

            val metadata = file.metadata().get()
            assertIs<RegularFileMetadata>(metadata)
            assertEquals(bytes.size.toLong(), metadata.size)

            assertContentEquals(bytes, file.readBytes().get())
        }
    }

    @Test
    fun `can random access a file to create it`() {
        val file = fixture.testDir.file("file")
        assertTrue(file.metadata().missing)

        val result = file.withContent { _ ->
            "result"
        }

        assertIs<Success<*>>(result)
        assertEquals("result", result.get())

        val metadata = file.metadata().get()
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(0, metadata.size)

        assertContentEquals(byteArrayOf(), file.readBytes().get())
    }

    @Test
    fun `can random access read bytes from a file to create it`() {
        val file = fixture.testDir.file("file")
        assertTrue(file.metadata().missing)

        val result = file.withContent { content ->
            assertEquals(0L, content.length())
            assertEquals(0L, content.currentPosition)

            val result = content.readStream.read(ByteArray(1024))
            assertEquals(EndOfStream, result)
            assertEquals(0L, content.currentPosition)

            "result"
        }

        assertIs<Success<*>>(result)
        assertEquals("result", result.get())

        val metadata = file.metadata().get()
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(0, metadata.size)

        assertContentEquals(byteArrayOf(), file.readBytes().get())
    }

    @Test
    fun `can write byte array to a file to create it`() {
        listOf("123".encodeToByteArray(), byteArrayOf()).forEachIndexed { index, bytes ->
            val file = fixture.testDir.file("file-$index")
            assertTrue(file.metadata().missing)

            file.writeBytes(bytes)

            val metadata = file.metadata().get()
            assertIs<RegularFileMetadata>(metadata)
            assertEquals(bytes.size.toLong(), metadata.size)

            assertContentEquals(bytes, file.readBytes().get())
        }
    }

    @Test
    fun `can write text to a file to create it`() {
        listOf("1234", "日本語", "").forEachIndexed { index, text ->
            val file = fixture.testDir.file("file-$index")

            assertTrue(file.metadata().missing)

            file.writeText(text)

            val metadata = file.metadata().get()
            assertIs<RegularFileMetadata>(metadata)
            assertEquals(text.encodeToByteArray().size.toLong(), metadata.size)

            assertEquals(text, file.readText().get())
        }
    }

    @Test
    fun `can stream bytes from file`() {
        val bytes = "123".encodeToByteArray()
        val file = fixture.testDir.file("file")
        file.writeBytes(bytes)

        val result = file.readBytes { stream ->
            val buffer = ByteArray(1024)
            val result = stream.read(buffer)
            assertIs<ReadBytes>(result)
            assertEquals(bytes.size, result.get())

            val result2 = stream.read(buffer)
            assertEquals(EndOfStream, result2)
            Success(buffer.take(bytes.size).toByteArray())
        }

        assertIs<Success<*>>(result)
        assertContentEquals(bytes, result.get())
    }

    @Test
    fun `can stream bytes from empty file`() {
        val bytes = byteArrayOf()
        val file = fixture.testDir.file("file")
        file.writeBytes(bytes)

        val result = file.readBytes { stream ->
            val buffer = ByteArray(1024)
            val result = stream.read(buffer)
            assertEquals(EndOfStream, result)
            Success("result")
        }

        assertIs<Success<*>>(result)
        assertEquals("result", result.get())
    }

    @Test
    fun `can stream nothing bytes from file`() {
        val file = fixture.testDir.file("file")
        file.writeBytes(byteArrayOf())

        val result = file.readBytes { _ -> Success("result") }

        assertIs<Success<*>>(result)
        assertEquals("result", result.get())
    }

    @Test
    fun `can random access read from file`() {
        val bytes = "1234567".encodeToByteArray()
        val file = fixture.testDir.file("file")
        file.writeBytes(bytes)

        val result = file.withContent { content ->
            content.seek(3)
            assertEquals(3L, content.currentPosition)

            val buffer = ByteArray(2)
            content.readStream.readFully(buffer, 0, 2)
            assertEquals("45", buffer.decodeToString())

            assertEquals(5L, content.currentPosition)

            "result"
        }

        assertIs<Success<*>>(result)
        assertEquals("result", result.get())
    }

    @Test
    fun `can random access write to file`() {
        val bytes = "123__67".encodeToByteArray()
        val file = fixture.testDir.file("file")
        file.writeBytes(bytes)

        val result = file.withContent { content ->
            content.seek(3)
            assertEquals(3L, content.currentPosition)

            val buffer = "45".encodeToByteArray()
            content.writeStream.write(buffer, 0, 2)

            assertEquals(5L, content.currentPosition)
            assertEquals(7L, content.length())

            "result"
        }

        assertIs<Success<*>>(result)
        assertEquals("result", result.get())

        assertEquals("1234567", file.readText().get())
    }

    @Test
    fun `can random access read and write to file`() {
        val file = fixture.testDir.file("file")

        val result = file.withContent { content ->
            content.writeStream.write("1234567".encodeToByteArray())

            assertEquals(7L, content.currentPosition)
            assertEquals(7L, content.length())

            content.seek(0)

            val buffer = CollectingBuffer()
            buffer.appendFrom(content.readStream)

            assertEquals("1234567", buffer.decodeToString())

            "result"
        }

        assertIs<Success<*>>(result)
        assertEquals("result", result.get())

        assertEquals("1234567", file.readText().get())
    }

    @Test
    fun `can random access append to file`() {
        val bytes = "1234".encodeToByteArray()
        val file = fixture.testDir.file("file")
        file.writeBytes(bytes)

        val result = file.withContent { content ->
            content.seekToEnd()
            assertEquals(4L, content.currentPosition)

            val buffer = "567".encodeToByteArray()
            content.writeStream.write(buffer)

            assertEquals(7L, content.currentPosition)
            assertEquals(7L, content.length())

            "result"
        }

        assertIs<Success<*>>(result)
        assertEquals("result", result.get())

        assertEquals("1234567", file.readText().get())
    }

    @Test
    fun `can write to file with unicode name`() {
        val file = fixture.testDir.file("日本語")
        file.writeText("1234")

        assertIs<RegularFileMetadata>(file.metadata().get())
        assertEquals("1234", file.readText().get())
    }

    @Test
    fun `can write text to an existing file to replace its contents`() {
        val file = fixture.testDir.file("file")
        file.writeText("1234")
        assertTrue(file.metadata().regularFile)

        file.writeText("abcdefgh")

        val metadata = file.metadata().get()
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(8, metadata.size)

        assertEquals("abcdefgh", file.readText().get())
    }

    @Test
    fun `can write long string to an existing file`() {
        val file = fixture.testDir.file("file")
        file.writeText("1234")
        assertTrue(file.metadata().regularFile)

        val longString = (0..2000).joinToString(",")

        file.writeText(longString)

        val metadata = file.metadata().get()
        assertIs<RegularFileMetadata>(metadata)
        assertEquals(longString.length.toLong(), metadata.size)

        assertEquals(longString, file.readText().get())
    }

    @Test
    fun `cannot write to a file that exists as a directory`() {
        val file = fixture.dir("dir1").toFile()

        for (action in writeActions) {
            try {
                action(file)
                fail()
            } catch (e: FileSystemException) {
                assertEquals("Could not write to $file as it is not a file.", e.message)
            }
        }
    }

    @Test
    fun `cannot write to a file whose parent does not exist`() {
        val parent = fixture.testDir.dir("dir1")
        val file = parent.file("file.txt")

        for (action in writeActions) {
            try {
                action(file)
                fail()
            } catch (e: FileSystemException) {
                assertEquals("Could not write to $file as directory $parent does not exist.", e.message)
            }
        }
    }

    @Test
    fun `cannot write to a file whose ancestor does not exist`() {
        val ancestor = fixture.testDir.dir("dir1")
        val file = ancestor.file("dir2/file.txt")

        for (action in writeActions) {
            try {
                action(file)
                fail()
            } catch (e: FileSystemException) {
                assertEquals("Could not write to $file as directory $ancestor does not exist.", e.message)
            }
        }
    }

    @Test
    fun `cannot write to a file whose parent is a file`() {
        val parent = fixture.file("dir1")
        val file = fixture.testDir.file("dir1/file.txt")

        for (action in writeActions) {
            try {
                action(file)
                fail()
            } catch (e: FileSystemException) {
                assertEquals("Could not write to $file as $parent exists but is not a directory.", e.message)
            }
        }
    }

    @Test
    fun `cannot write to a file whose ancestor is a file`() {
        val ancestor = fixture.file("dir1")
        val file = fixture.testDir.file("dir1/dir2/file.txt")

        for (action in writeActions) {
            try {
                action(file)
                fail()
            } catch (e: FileSystemException) {
                assertEquals("Could not write to $file as $ancestor exists but is not a directory.", e.message)
            }
        }
    }

    @Test
    fun `cannot read from a file that does not exist`() {
        val file = fixture.testDir.file("missing")

        for (action in readActions) {
            val result = action(file)
            assertIs<MissingEntry<*>>(result)
            try {
                result.get()
            } catch (e: IOException) {
                assertEquals("Could not read from file $file as it does not exist.", e.message)
            }
        }
    }

    @Test
    fun `cannot read from a file that exists as a directory`() {
        val file = fixture.dir("dir1").toFile()

        for (action in readActionsThatStream) {
            val result = action(file)
            assertIs<FailedOperation<*>>(result)
            try {
                result.get()
            } catch (e: IOException) {
                assertEquals("Could not read from file $file as it is not a file.", e.message)
            }
        }
    }

    @Test
    fun `can delete a file`() {
        val file = fixture.file("file.txt")
        assertTrue(file.metadata().regularFile)

        file.delete()
        assertTrue(file.metadata().missing)
    }

    @Test
    fun `cannot delete a file that exists as a directory`() {
        val file = fixture.dir("empty").toFile()

        try {
            file.delete()
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not delete file $file as it is not a file.", e.message)
        }
    }

    @Test
    fun `cannot delete a file that exists as a symlink`() {
        val file = fixture.symlink("link", "something").toFile()

        try {
            file.delete()
            fail()
        } catch (e: FileSystemException) {
            assertEquals("Could not delete file $file as it is not a file.", e.message)
        }
    }

    @Test
    fun `can make file executable`() {
        val file = fixture.file("file")
        if (!file.supports(FileSystemCapability.PosixPermissions)) {
            return
        }

        assertFalse(file.posixPermissions().get().isOwnerExecutable)

        file.setPermissions(file.posixPermissions().get().executable())
        assertTrue(file.posixPermissions().get().isOwnerExecutable)
    }
}