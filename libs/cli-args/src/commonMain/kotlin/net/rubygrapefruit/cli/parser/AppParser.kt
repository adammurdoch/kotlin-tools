package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.Output

internal class AppParser(
    private val name: String,
    private val parser: Parser<Runnable>,
    private val help: Boolean = false,
    private val completion: Boolean = false,
) : AbstractParser<Runnable>(), Application {
    override fun withHelp(): Application {
        return AppParser(name, parser, true, completion)
    }

    override fun withCompletion(): Application {
        return AppParser(name, parser, help, true)
    }

    override fun start(): ParseState<Runnable> {
        return parser().start()
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
        return params(option("h", "help").boolean().required()) { HelpAction(this) }
    }

    private fun completionAction(): Parser<Runnable> {
        return params(option("completion").boolean().required()) { CompletionAction() }
    }

    private inner class AppUsage(val usage: UsageInternal) : UsageInternal {
        override fun choices(): List<UsageInternal> {
            return usage.choices().map { AppUsage(it) }
        }

        override fun writeTo(quoted: Boolean, output: Output) {
            output.write(name)
            if (!usage.isEmpty) {
                output.write(" ")
                usage.writeTo(quoted, output)
            }
        }
    }
}