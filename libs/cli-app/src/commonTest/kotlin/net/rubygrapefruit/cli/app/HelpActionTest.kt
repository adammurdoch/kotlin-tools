package net.rubygrapefruit.cli.app

import kotlin.test.Test
import kotlin.test.assertEquals

class HelpActionTest : AbstractActionTest() {
    @Test
    fun `generates help output for app with no configuration`() {
        val app = CliApp("cmd")
        val help = HelpAction(app)

        assertEquals(
            """
            Usage: cmd

        """.trimIndent(), help.formatted
        )
    }

    @Test
    fun `generated help output for app with multiple parameters`() {
        class App : CliApp("cmd") {
            val p1 by parameter("z", help = "some value")
            val p2 by parameter("another-param", help = "some other value")
            val p3 by parameter("no-help")
        }

        val app = App()
        val help = HelpAction(app)

        assertEquals(
            """
            Usage: cmd <z> <another-param> <no-help>
            
            Parameters:
              <another-param> some other value
              <z>             some value
            
            """.trimIndent(), help.formatted
        )
    }

}