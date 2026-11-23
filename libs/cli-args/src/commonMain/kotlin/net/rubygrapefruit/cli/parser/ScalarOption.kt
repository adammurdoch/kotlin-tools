package net.rubygrapefruit.cli.parser

/**
 * A parser that matches an option with a string value.
 *
 * The parser converts the option value to [T] or produces `null` when the option is not present in the arguments.
 */
interface ScalarOption<out T> : Option<T?> {
    /**
     * Creates a copy of this option that must be present in the provided arguments.
     */
    fun required(): Option<T>

    /**
     * Creates a copy of this option that produces `true` when the option is present in the arguments or `false` when the option is not present.
     */
    fun boolean(): BooleanOption
}