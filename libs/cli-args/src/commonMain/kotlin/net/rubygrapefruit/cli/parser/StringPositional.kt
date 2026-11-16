package net.rubygrapefruit.cli.parser

/**
 * A parser that matches a single positional argument.
 *
 * Produces the positional argument as its result. The argument must be present.
 */
interface StringPositional : Positional<String> {
    /**
     * Returns a copy of this positional that matches one or more arguments.
     */
    fun oneOrMore(): Positional<List<String>>

    /**
     * Returns a copy of this positional that matches zero or more arguments.
     */
    fun zeroOrMore(): Positional<List<String>>
}