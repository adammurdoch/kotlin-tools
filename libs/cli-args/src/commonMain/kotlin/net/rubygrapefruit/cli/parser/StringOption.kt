package net.rubygrapefruit.cli.parser

/**
 * A parser that matches an option with a string value.
 *
 * Produces the option value as its result or produces `null` when the option is not present in the arguments.
 */
interface StringOption : Option<String?> {
    /**
     * Creates a copy of this option that produces `true` when the option is present in the arguments or `false` when the option is not present.
     */
    fun boolean(): BooleanOption
}