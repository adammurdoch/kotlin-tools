package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.Output

internal class HelpAction(private val parser: ParserInternal<*>) : Runnable {
    override fun Runnable.Context.run() {
        run(output)
    }

    fun run(output: Output) {
        val usage = parser.usage()
        output.apply {
            usage.writeHelpHeaderTo(this)

            val choices = usage.choices()
            output.writeLine()
            output.heading("USAGE:")
            output.writeLine()
            for (choice in choices) {
                output.write("  ")
                choice.writeTo(false, output)
                choice.writeSummaryTo(output)
                output.writeLine()
            }

            val options = usage.options()
            if (options.isNotEmpty()) {
                writeLine()
                heading("OPTIONS:")
                writeLine()

                for (option in options) {
                    write("  ")
                    option.writeOptionUsageTo(this)
                    writeLine()
                }
            }
            writeLine()
        }
    }
}