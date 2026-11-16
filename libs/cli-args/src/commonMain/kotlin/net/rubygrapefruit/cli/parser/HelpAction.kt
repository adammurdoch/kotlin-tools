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
            definitionList {
                for (choice in choices) {
                    item {
                        term {
                            choice.writeTo(false, this)
                        }
                        description {
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
                definitionList {
                    for (positional in positionals) {
                        item {
                            term {
                                positional.writeTo(false, this)
                            }
                            description {
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
                definitionList {
                    for (option in options) {
                        item {
                            term {
                                option.writeOptionUsageTo(this)
                            }
                            description {
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