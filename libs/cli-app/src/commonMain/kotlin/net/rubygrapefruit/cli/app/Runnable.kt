package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.parser.Parser

interface Runnable {
    fun run()
}

/**
 * Runs the [Runnable] produced by parsing the given arguments, and then exits the process.
 */
fun Parser<Runnable>.runApp(arguments: Array<String>) {
    val exitCode = runApp(this, arguments.toList())
    exit(exitCode)
}

/**
 * Parses args and runs app. Returns the exit code.
 */
internal fun runApp(parser: Parser<Runnable>, arguments: List<String>): Int {
    return try {
        parser.parse(arguments.toList()).get().run()
        0
    } catch (e: Throwable) {
        e.printStackTrace()
        1
    }
}