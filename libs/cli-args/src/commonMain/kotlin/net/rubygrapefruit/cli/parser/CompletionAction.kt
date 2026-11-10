package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.Output
import net.rubygrapefruit.cli.terminal.output

internal class CompletionAction(private val appName: String) : Runnable {
    override fun run() {
        run(output())
    }

    fun run(output: Output) {
        output.apply {
            writeLine("# Generated ZSH completion script")
            writeLine("# Use `source <this-script>` to enable completion for application '$appName'")

            val functionName = "_${appName}_complete"

            writeLine()
            writeLine("compdef $functionName $appName")

            writeLine()
            writeLine("function $functionName() {")
            writeLine("}")
        }
    }
}