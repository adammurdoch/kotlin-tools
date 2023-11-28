package net.rubygrapefruit.file

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

abstract class AbstractFileSystemElementTest<T : FileSystemElement> : AbstractFileTest() {
    abstract fun create(name: String): T

    abstract fun missing(name: String): T

    open fun canSetPermissions(element: T): Boolean {
        return element.supports(FileSystemCapability.PosixPermissions)
    }

    open fun canQueryPermissions(element: T): Boolean {
        return element.supports(FileSystemCapability.PosixPermissions)
    }

    @Test
    fun `can query path elements`() {
        val element = create("file")
        assertEquals("file", element.name)
        assertEquals(element.name, element.path.name)
        assertEquals(element.absolutePath, element.path.absolutePath)
        assertEquals(element.absolutePath, element.toString())
        assertEquals(element.absolutePath, element.path.toString())
    }

    @Test
    fun `can query posix permissions`() {
        val element = create("file")
        if (!canQueryPermissions(element)) {
            return
        }

        element.posixPermissions().get()
    }

    @Test
    fun `can set and query posix permissions`() {
        val element = create("file")
        if (!canSetPermissions(element)) {
            return
        }

        assertNotEquals(PosixPermissions.readOnlyFile, element.posixPermissions().get())

        element.setPermissions(PosixPermissions.readOnlyFile)
        assertEquals(PosixPermissions.readOnlyFile, element.posixPermissions().get())
    }

    @Test
    fun `cannot query posix permissions when not supported`() {
        val element = create("file")
        if (canQueryPermissions(element)) {
            return
        }

        val result = element.posixPermissions()
        assertIs<UnsupportedOperation<*>>(result)
        try {
            result.get()
        } catch (e: FileSystemException) {
            assertEquals("Could not read POSIX permissions for $element as it is not supported by this filesystem.", e.message)
        }
    }

    @Test
    fun `cannot set file posix permissions when not supported`() {
        val element = create("file")
        if (canSetPermissions(element)) {
            return
        }

        try {
            element.setPermissions(PosixPermissions.readOnlyFile)
        } catch (e: FileSystemException) {
            assertEquals("Could not set POSIX permissions on $element as it is not supported by this filesystem.", e.message)
        }
    }
}