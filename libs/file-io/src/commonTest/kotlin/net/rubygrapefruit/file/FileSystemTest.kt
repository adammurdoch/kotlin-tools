package net.rubygrapefruit.file

import kotlin.test.Test
import kotlin.test.assertNotNull

class FileSystemTest {
    @Test
    fun `can query home and current directories`() {
        assertNotNull(fileSystem.currentDirectory)
        assertNotNull(fileSystem.userHomeDirectory)
    }
}