package net.rubygrapefruit.cli.parser

/**
 * A parser that matches positional parameters, and produces a result of type [T].
 */
interface Positional<out T> : Parser<T> {
}