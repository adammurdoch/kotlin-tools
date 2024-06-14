package net.rubygrapefruit.cli

internal class HelpAction(val action: Action): Action() {
    override fun run() {
        println("this is the usage message.")
    }
}