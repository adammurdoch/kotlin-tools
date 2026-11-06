package net.rubygrapefruit.cli.parser

/**
 * A parser that matches a single positional parameter and produces a result of type [T].
 */
interface Positional<out T> : Parser<T> {
}