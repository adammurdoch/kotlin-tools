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
            writeLine()
            heading("USAGE:")
            writeLine()
            for (choice in choices) {
                write("  ")
                choice.writeTo(false, this)
                choice.writeSummaryTo(this)
                writeLine()
            }

            val positionals = usage.positionals()
            if (positionals.isNotEmpty()) {
                writeLine()
                heading("PARAMETERS:")
                writeLine()

                for (positional in positionals) {
                    write("  ")
                    positional.writeTo(false, this)
                    positional.writePositionalSummaryTo(this)
                    writeLine()
                }
            }

            val options = usage.options()
            if (options.isNotEmpty()) {
                writeLine()
                heading("OPTIONS:")
                writeLine()

                for (option in options) {
                    write("  ")
                    option.writeOptionUsageTo(this)
                    option.writeOptionSummaryTo(this)
                    writeLine()
                }
            }
            writeLine()
        }
    }
}