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
            table {
                for (choice in choices) {
                    row {
                        cell {
                            choice.writeTo(false, this)
                        }
                        cell {
                            choice.writeSummaryTo(this)
                        }
                    }
                }
            }

            val positionals = usage.positionals()
            if (positionals.isNotEmpty()) {
                writeLine()
                heading("PARAMETERS:")
                writeLine()
                table {
                    for (positional in positionals) {
                        row {
                            cell {
                                positional.writeTo(false, this)
                            }
                            cell {
                                positional.writePositionalSummaryTo(this)
                            }
                        }
                    }
                }
            }

            val options = usage.options()
            if (options.isNotEmpty()) {
                writeLine()
                heading("OPTIONS:")
                writeLine()
                table {
                    for (option in options) {
                        row {
                            cell {
                                option.writeOptionUsageTo(this)
                            }
                            cell {
                                option.writeOptionSummaryTo(this)
                            }
                        }
                    }
                }
            }

            writeLine()
        }
    }
}