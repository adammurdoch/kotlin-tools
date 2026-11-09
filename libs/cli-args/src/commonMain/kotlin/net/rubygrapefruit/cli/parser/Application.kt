package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable

interface Application : Parser<Runnable> {
    fun withHelp(): Application
}