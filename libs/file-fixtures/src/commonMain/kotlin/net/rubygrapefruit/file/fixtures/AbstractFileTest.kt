package net.rubygrapefruit.file.fixtures

import net.rubygrapefruit.file.ElementType
import net.rubygrapefruit.file.FileSystemCapability
import net.rubygrapefruit.file.PosixPermissions
import kotlin.test.AfterTest

abstract class AbstractFileTest {
    protected val fixture = FilesFixture()

    @AfterTest
    fun cleanup() {
        // Try to reset the test file permissions
        if (fixture.testDir.supports(FileSystemCapability.PosixPermissions)) {
            fixture.testDir.visitTopDown {
                when (type) {
                    ElementType.Directory -> toDir().setPermissions(PosixPermissions.readWriteDirectory)
                    ElementType.SymLink -> {
                        val element = toSymLink()
                        if (element.supports(FileSystemCapability.SetSymLinkPosixPermissions)) {
                            element.setPermissions(PosixPermissions.readWriteFile)
                        }
                    }

                    ElementType.RegularFile -> toFile().setPermissions(PosixPermissions.readWriteFile)
                    ElementType.Other -> {}
                }
            }
        }

        fixture.testDir.deleteRecursively()
    }
}