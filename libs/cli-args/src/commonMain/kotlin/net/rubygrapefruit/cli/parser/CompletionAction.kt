package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable

internal class CompletionAction : Runnable {
    override fun run() {
        println("this is the completion")
    }
}