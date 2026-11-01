package net.rubygrapefruit.cli.app

import net.rubygrapefruit.app.launcher.isPropagateMainFailure
import net.rubygrapefruit.cli.parser.Parser
import net.rubygrapefruit.cli.terminal.Output
import net.rubygrapefruit.cli.terminal.output

interface Runnable {
    fun run()
}

/**
 * Runs the [Runnable] produced by parsing the given arguments, and then exits the process.
 */
fun Parser<Runnable>.runApp(arguments: Array<String>) {
    val exitCode = runApp(this, arguments.toList(), output())
    exit(exitCode)
}

/**
 * Parses args and runs app. Returns the exit code.
 */
internal fun runApp(parser: Parser<Runnable>, arguments: List<String>, output: Output): Int {
    try {
        val result = parser.parse(arguments.toList())
        if (result is Parser.Failure) {
            result.writeTo(output)
            if (isPropagateMainFailure) {
                result.get()
            }
            return 1
        }
        result.get().run()
        return 0
    } catch (t: Throwable) {
        if (isPropagateMainFailure) {
            throw t
        } else {
            val message = t.message
            if (message.isNullOrEmpty()) {
                output.append("$t (no message)")
            } else {
                output.append(message)
            }
            output.maybeNewLine()
        }
        return 1
    }
}