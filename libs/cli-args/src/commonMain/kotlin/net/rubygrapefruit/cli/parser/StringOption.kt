package net.rubygrapefruit.cli.parser

/**
 * An option that produces a string value or null when the option is not provided.
 */
interface StringOption : Option<String?> {
    /**
     * Creates a copy of this option that produces `true` when the option is provided or `false` when the option is not provided.
     */
    fun boolean(): Option<Boolean>
}