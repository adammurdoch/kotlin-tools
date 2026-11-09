package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.Output

internal class AppParser<OUT : Runnable>(
    private val name: String,
    private val parser: Parser<OUT>,
    private val help: Boolean = false
) : AbstractParser<OUT>(), Application<OUT> {
    override fun withHelp(): Application<Runnable> {
        return AppParser(name, parser, true)
    }

    override fun start(): ParseState<OUT> {
        return parser().start()
    }

    override fun usage(): UsageInternal {
        return AppUsage(parser().usage())
    }

    private fun parser(): ParserInternal<OUT> {
        return if (help) {
            lateinit var fullParser: ParserInternal<OUT>
            val helpOption = params(option("h", "help").boolean().required()) { HelpAction(fullParser) }
            fullParser = oneOf(helpOption, parser) as ParserInternal<OUT>
            fullParser
        } else {
            parser.cast()
        }
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