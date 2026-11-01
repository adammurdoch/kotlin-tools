package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

/**
 * Parses a list of arguments to produce a result of type [T].
 */
sealed interface Parser<out T> {
    /**
     * Parse the given arguments.
     */
    fun parse(args: List<String>): Result<T>

    /**
     * Parse the given arguments.
     */
    fun parse(vararg args: String): Result<T> {
        return parse(args.toList())
    }

    sealed interface Result<out T> {
        /**
         * Return the result or throw an exception.
         */
        fun get(): T
    }

    data class Problem(val index: Int, val message: String)

    /**
     * Provides information about argument parsing failure.
     */
    sealed interface Failure : Result<Nothing> {
        val problems: List<Problem>

        fun writeTo(output: Output)
    }

    /**
     * Provides the result of argument parsing.
     */
    sealed interface Success<out T> : Result<T> {
        val result: T
    }
}