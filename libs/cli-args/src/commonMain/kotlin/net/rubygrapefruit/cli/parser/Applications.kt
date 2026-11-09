package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable

/**
 * Creates an application command-line argument parser from the given parser.
 */
fun <OUT: Runnable> app(name: String, parser: Parser<OUT>): Application<OUT> {
    return AppParser(name, parser)
}

