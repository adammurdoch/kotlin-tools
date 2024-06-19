package net.rubygrapefruit.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class ArgParseExceptionTest {
    @Test
    fun `formats available actions`() {
        val action = ActionUsage(null, emptyList(), emptyList())
        val exception = ArgParseException(
            "broken",
            "Please run something",
            listOf(
                SubActionUsage("run", "run something", action),
                SubActionUsage("do-another-thing", "run something else", action),
                SubActionUsage("no-help", null, action)
            )
        )

        assertEquals("""
            Please run something
            
            Available actions:
              do-another-thing run something else
              no-help
              run              run something
        """.trimIndent(), exception.formattedMessage)
    }
}