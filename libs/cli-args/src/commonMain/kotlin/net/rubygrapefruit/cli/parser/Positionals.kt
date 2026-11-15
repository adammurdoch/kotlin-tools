package net.rubygrapefruit.cli.parser

/**
 * Creates a positional parameter with the given name.
 *
 * The returned parser will match a single argument that isn't an option.
 */
fun positional(name: String, summary: String? = null): Positional<String> {
    return DefaultPositional(name, summary)
}

/**
 * Creates a parser that matches the given text.
 */
fun command(name: String): Parser<Unit> {
    if (name.isEmpty()) {
        throw IllegalArgumentException("Command name cannot be empty")
    }
    if (name.startsWith("-")) {
        throw IllegalArgumentException("Command name '$name' cannot start with '-'")
    }
    if (name.contains(" ")) {
        throw IllegalArgumentException("Command name '$name' cannot contain whitespace")
    }
    return DefaultCommand(name)
}