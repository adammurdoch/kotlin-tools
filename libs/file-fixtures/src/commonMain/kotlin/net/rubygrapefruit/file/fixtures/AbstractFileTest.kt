package net.rubygrapefruit.file.fixtures

import kotlin.test.AfterTest

abstract class AbstractFileTest {
    protected val fixture = FilesFixture()

    @AfterTest
    fun cleanup() {
        fixture.cleanup()
    }
}