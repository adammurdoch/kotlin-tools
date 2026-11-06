package net.rubygrapefruit.cli.parser

/**
 * Creates a positional parameter with the given name.
 *
 * The returned parser will match a single argument that isn't an option.
 */
fun positional(name: String): Positional<String> {
    return DefaultParameter(name)
}

/**
 * Creates a parser that matches the given text.
 */
fun command(value: String): Parser<Unit> {
    return DefaultCommand(value)
}