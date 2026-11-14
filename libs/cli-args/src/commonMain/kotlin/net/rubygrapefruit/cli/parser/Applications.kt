package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable

/**
 * Creates an application command-line argument parser from the given parser.
 */
fun <OUT : Runnable> app(
    name: String,
    parser: Parser<OUT>,
    helpAction: Boolean = true,
    completionAction: Boolean = true
): Parser<Runnable> {
    return AppParser(name, parser, helpAction, completionAction)
}

