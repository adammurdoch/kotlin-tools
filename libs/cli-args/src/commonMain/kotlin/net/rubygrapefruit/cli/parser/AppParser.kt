package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class AppParser<T>(private val name: String, private val parser: ParserInternal<T>) : AbstractParser<T>(), Parser<T> {
    override fun usage(): UsageInternal {
        return AppUsage(parser.usage())
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

    override fun start(): ParseState<T> {
        return parser.start()
    }
}