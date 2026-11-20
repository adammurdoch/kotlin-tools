package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.Output

internal class AppParser(
    private val name: String,
    private val parser: Parser<Runnable>,
    private val summary: String?,
    private val help: Boolean,
    private val completion: Boolean,
) : AbstractParser<Runnable>(), Parser<Runnable> {

    override fun edges(consumer: (ParseEdge) -> Unit) {
        parser().edges(consumer)
    }

    override fun usage(): UsageInternal {
        return AppUsage(parser().usage())
    }

    private fun parser(): ParserInternal<Runnable> {
        return if (help && !completion) {
            val helpOption = helpAction()
            oneOf(helpOption, parser).cast()
        } else if (!help && completion) {
            val completionOption = completionAction()
            oneOf(completionOption, parser).cast()
        } else if (help && completion) {
            val helpOption = helpAction()
            val completionOption = completionAction()
            oneOf(helpOption, completionOption, parser).cast()
        } else {
            parser.cast()
        }
    }

    private fun helpAction(): Parser<Runnable> {
        return params(
            option("h", "help", summary = "Show usage information").boolean().required(),
            summary = "Show $name usage information"
        ) { HelpAction(this) }
    }

    private fun completionAction(): Parser<Runnable> {
        return params(
            option("completion", summary = "Generate completion script").boolean().required(),
            summary = "Generate a ZSH completion script for $name"
        ) { CompletionAction(name) }
    }

    private inner class AppUsage(val usage: UsageInternal) : UsageInternal {
        override fun choices(): List<UsageInternal> {
            return usage.choices().map { AppUsage(it) }
        }

        override fun options(): List<UsageInternal.OptionUsage> {
            return usage.options()
        }

        override fun positionals(): List<UsageInternal.PositionalUsage> {
            return usage.positionals()
        }

        override fun writeHelpHeaderTo(output: Output) {
            if (summary != null) {
                output.writeLine()
                output.write(name)
                output.write(" - ")
                output.write(summary)
                output.maybeNewLine()
            }
        }

        override fun writeTo(quoted: Boolean, output: Output) {
            output.write(name)
            if (!usage.isEmpty) {
                output.write(" ")
                usage.writeTo(quoted, output)
            }
        }

        override fun writeSummaryTo(output: Output) {
            usage.writeSummaryTo(output)
        }
    }
}