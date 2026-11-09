package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable

interface Application<out T : Runnable> : Parser<T> {
    fun withHelp(): Application<Runnable>
}