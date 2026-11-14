package net.rubygrapefruit.cli.app

import net.rubygrapefruit.app.launcher.isPropagateMainFailure
import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.parser.Parser
import net.rubygrapefruit.cli.terminal.Output
import net.rubygrapefruit.cli.terminal.diagnostics
import net.rubygrapefruit.cli.terminal.output

/**
 * Runs the [Runnable] produced by parsing the given arguments, and then exits the process.
 */
fun appMain(arguments: Array<String>, factory: () -> Parser<Runnable>) {
    val exitCode = runApp(arguments.toList(), diagnostics(), factory)
    exit(exitCode)
}

/**
 * Parses args and runs app. Returns the exit code.
 */
internal fun runApp(arguments: List<String>, diagnostics: Output, factory: () -> Parser<Runnable>): Int {
    try {
        val result = parseArgs(factory, arguments)
        if (result is Parser.Failure) {
            result.writeTo(diagnostics)
            if (isPropagateMainFailure) {
                result.get()
            }
            return 1
        }
        result.get().run {
            DefaultContext().run()
        }
        return 0
    } catch (t: Throwable) {
        if (isPropagateMainFailure) {
            throw t
        } else {
            val message = t.message
            if (message.isNullOrEmpty()) {
                diagnostics.write("$t (no message)")
            } else {
                diagnostics.write(message)
            }
            diagnostics.maybeNewLine()
        }
        return 1
    }
}

private fun parseArgs(factory: () -> Parser<Runnable>, arguments: List<String>): Parser.Result<Runnable> {
    val parser = factory()
    val result = parser.parse(arguments.toList())
    return result
}

private class DefaultContext : Runnable.Context {
    override val output: Output
        get() = output()

    override val diagnostics: Output
        get() = diagnostics()
}