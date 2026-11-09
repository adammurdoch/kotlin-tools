package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable

/**
 * Creates an application command-line argument parser from the given parser.
 */
fun app(name: String, parser: Parser<Runnable>): Application {
    return AppParser(name, parser)
}

