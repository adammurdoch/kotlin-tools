package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.Output
import net.rubygrapefruit.cli.terminal.output

internal class HelpAction(private val parser: ParserInternal<*>) : Runnable {
    override fun run() {
        run(output())
    }

    fun run(output: Output) {
        val usage = parser.usage()
        output.apply {
            writeLine()
            usage.writeSummaryTo(this)
            maybeNewLine()
            val options = usage.options()
            if (options.isNotEmpty()) {
                heading("OPTIONS")
                writeLine()

                for (option in options) {
                    write("  ")
                    option.writeOptionUsageTo(this)
                    writeLine()
                }
            }
        }
    }
}