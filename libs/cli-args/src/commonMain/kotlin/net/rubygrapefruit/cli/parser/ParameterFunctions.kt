package net.rubygrapefruit.cli.parser

/**
 * Creates a parameter with the given name.
 *
 * The returned parser will match any input value that isn't an option.
 */
fun parameter(name: String): Parameter<String> {
    return DefaultParameter(name)
}

/**
 * Creates a parser that matches the given text.
 */
fun literal(value: String): Parser<Unit> {
    return DefaultLiteral(value)
}