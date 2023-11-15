package net.rubygrapefruit.file

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.AfterTest

abstract class AbstractFileSystemElementTest {
    protected val fixture = FilesFixture()

    @AfterTest
    fun cleanup() {
        fixture.testDir.visitTopDown {
            // Try to reset the test file permissions
            when (type) {
                ElementType.Directory -> toDir().setPermissions(PosixPermissions.readWriteDirectory)
                ElementType.SymLink -> {
                    val element = toElement();
                    if (element.supports(FileSystemCapability.SetSymLinkPosixPermissions)) {
                        toElement().setPermissions(PosixPermissions.readWriteFile)
                    }
                }
                else -> toElement().setPermissions(PosixPermissions.readWriteFile)
            }
        }
    }
}