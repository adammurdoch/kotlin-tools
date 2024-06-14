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
                SubActionInfo("run", "run something"),
                SubActionInfo("do-another-thing", "run something else"),
                SubActionInfo("no-help", null)
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