package net.rubygrapefruit.cli.parser

/**
 * Creates an option with the given name.
 *
 * The returned parser will match `-<name> <string>` or empty input.
 */
fun option(name: String): Option<String?> {
    return DefaultOption(name)
}