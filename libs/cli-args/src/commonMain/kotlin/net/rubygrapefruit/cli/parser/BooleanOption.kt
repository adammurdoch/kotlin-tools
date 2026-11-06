package net.rubygrapefruit.cli.parser

/**
 * A parser that matches an option with no value.
 *
 * Produces `true` when the option is present in the arguments, or `false` when the option is missing.
 */
interface BooleanOption : Option<Boolean> {
    /**
     * Creates a copy of this option that must be present in the provided arguments.
     */
    fun required(): Parser<Unit>
}