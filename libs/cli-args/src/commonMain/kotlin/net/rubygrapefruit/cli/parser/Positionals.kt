package net.rubygrapefruit.cli.parser

/**
 * Creates a parser that matches a single argument that isn't an option. The parser produces the matched argument as its value.
 *
 * @param name Used for diagnostics and usage messages.
 */
fun positional(name: String, summary: String? = null): ScalarPositional<String> {
    return positional(name, IdentityStringType, summary)
}

/**
 * Creates a parser that matches a single argument that isn't an option. The parser converts the matched argument to the given type.
 *
 * @param name Used for diagnostics and usage messages.
 */
fun <OUT> positional(name: String, type: StringType<OUT>, summary: String? = null): ScalarPositional<OUT> {
    return DefaultPositional(name, type as StringTypeInternal<OUT>, summary)
}

/**
 * Creates a parser that matches any single argument, include any option. The parser produces the matched argument as its value.
 */
fun anything(name: String, summary: String? = null): ScalarPositional<String> {
    return AnythingParser(name, summary)
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
