package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ArgParseExceptionTest {
    @Test
    fun `formats available actions`() {
        val exception = ArgParseException(
            "broken",
            "Please run something",
            listOf(
                SubActionUsage("run", "run something"),
                SubActionUsage("do-another-thing", "run something else"),
                SubActionUsage("no-help", null)
            )
        )

        assertEquals("""
            Please run something
            
            Available actions:
              run              run something
              do-another-thing run something else
              no-help

        """.trimIndent(), exception.formattedMessage)
    }
}