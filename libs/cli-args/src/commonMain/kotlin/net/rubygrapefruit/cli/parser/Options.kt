package net.rubygrapefruit.cli.parser

/**
 * Creates an option with the given name.
 *
 * The returned parser will match `-n <string>` or `--name <string>` or empty input.
 */
fun option(
    name: String,
    vararg otherNames: String,
    summary: String? = null
): StringOption {
    return DefaultStringOption(listOf(name) + otherNames.toList(), summary)
}