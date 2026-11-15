package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable

/**
 * Creates an application command-line argument parser from the given parser.
 *
 * @param name The name of the CLI app
 * @param summary A short description of what the app does
 */
fun app(
    name: String,
    parser: Parser<Runnable>,
    summary: String? = null,
    helpAction: Boolean = true,
    completionAction: Boolean = true,
): Parser<Runnable> {
    return AppParser(name, parser, summary, helpAction, completionAction)
}
