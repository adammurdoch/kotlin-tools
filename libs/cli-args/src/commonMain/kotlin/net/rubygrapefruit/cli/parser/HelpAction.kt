package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable

internal class HelpAction : Runnable {
    override fun run() {
        println("this is the help")
    }
}