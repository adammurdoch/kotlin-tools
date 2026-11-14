package net.rubygrapefruit.cli

import net.rubygrapefruit.cli.terminal.Output

interface Runnable {
    fun Context.run()

    interface Context {
        val output: Output
        val diagnostics: Output
    }
}