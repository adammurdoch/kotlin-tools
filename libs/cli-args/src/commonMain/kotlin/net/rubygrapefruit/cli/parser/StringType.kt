package net.rubygrapefruit.cli.parser

/**
 * Represents values of type [T] that can be produced from a string argument.
 */
interface StringType<out T> {
    sealed interface Result<out T>

    class Success<T>(val value: T) : Result<T>
    object Failure : Result<Nothing>

}