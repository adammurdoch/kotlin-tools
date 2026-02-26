package net.rubygrapefruit.parse

import net.rubygrapefruit.file.fixtures.FilesFixture
import kotlin.test.AfterTest

abstract class AbstractFileParseTest : AbstractParseTest() {
    val fixture = FilesFixture()

    @AfterTest
    fun cleanup() {
        fixture.cleanup()
    }
}