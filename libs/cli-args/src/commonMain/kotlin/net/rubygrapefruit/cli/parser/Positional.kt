package net.rubygrapefruit.cli.parser

/**
 * A parser that matches arguments by their position and produces a result of type [T].
 */
interface Positional<out T> : Parser<T>