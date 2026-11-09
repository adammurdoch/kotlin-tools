package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.Output

internal class AppParser<OUT : Runnable>(private val name: String, private val parser: Parser<OUT>) : AbstractParser<OUT>(), Application<OUT> {
    override fun withHelp(): Application<Runnable> {
        val parser = oneOf(
            params(option("h", "help").boolean().required()) { HelpAction() },
            parser
        )
        return AppParser(name, parser)
    }

    override fun usage(): UsageInternal {
        return AppUsage(parser.cast().usage())
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

    override fun start(): ParseState<OUT> {
        return parser.cast().start()
    }
}