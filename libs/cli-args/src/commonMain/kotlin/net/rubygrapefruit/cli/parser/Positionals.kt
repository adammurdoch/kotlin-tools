package net.rubygrapefruit.cli.parser

/**
 * Creates a parser that matches a single argument that isn't an option. The parser produces the matched argument as its value.
 *
 * @param name Used for diagnostics and usage messages.
 */
fun positional(name: String, summary: String? = null): StringPositional {
    return DefaultStringPositional(name, summary)
}

/**
 * Creates a parser that matches the given command name. The parser produces `Unit` as its result.
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

/**
 * Creates a parser that matches any single argument, include any option. The parser produces the matched argument as its value.
 */
fun anything(): StringPositional {
    return AnythingParser()
}