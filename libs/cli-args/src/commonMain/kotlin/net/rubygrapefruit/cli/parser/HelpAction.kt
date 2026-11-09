package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.Output
import net.rubygrapefruit.cli.terminal.output

internal class HelpAction(private val parser: ParserInternal<*>) : Runnable {
    override fun run() {
        run(output())
    }

    fun run(output: Output) {
        output.writeLine()
        parser.usage().writeTo(output)
        output.maybeNewLine()
    }
}