package net.rubygrapefruit.cli.parser

/**
 * A parser that matches an option, possibly with a value, and produces a result of type [T].
 */
interface Option<out T>: Parser<T> {
}