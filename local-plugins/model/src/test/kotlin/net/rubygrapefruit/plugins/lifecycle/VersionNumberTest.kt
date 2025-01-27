package net.rubygrapefruit.plugins.lifecycle

import kotlin.test.Test
import kotlin.test.assertEquals

class VersionNumberTest {
    @Test
    fun `parses version string`() {
        assertEquals("1.0", VersionNumber.of("1.0").version)
        assertEquals("12", VersionNumber.of("12").version)
        assertEquals("0.1", VersionNumber.of("0.1").version)
        assertEquals("0.0.2-milestone-2", VersionNumber.of("0.0.2-milestone-2").version)
        assertEquals("2.0-rc-1", VersionNumber.of("2.0-rc-1").version)
    }

    @Test
    fun `calculates next milestone`() {
        assertEquals("1.1-milestone-1", VersionNumber.of("1.0").nextMilestone().version)
        assertEquals("0.0.2-milestone-2", VersionNumber.of("0.0.2-milestone-1").nextMilestone().version)
    }

    @Test
    fun `calculates final version`() {
        assertEquals("1.0", VersionNumber.of("1.0").final().version)
        assertEquals("0.0.2", VersionNumber.of("0.0.2-milestone-1").final().version)
    }

    @Test
    fun `calculates released version`() {
        assertEquals("1.0", VersionNumber.of("1.0").released().version)
        assertEquals("0.0.1", VersionNumber.of("0.0.2-milestone-1").released().version)
        assertEquals("1.0", VersionNumber.of("1.0-milestone-1").released().version)
    }
}