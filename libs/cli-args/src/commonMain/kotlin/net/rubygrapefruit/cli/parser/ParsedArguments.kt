package net.rubygrapefruit.cli.parser

internal sealed interface ParsedArguments<out T> {
    /**
     * Attempts to produce a [Failure] for the first unrecognized argument.
     */
    fun claimUnrecognized(args: InputArgs): Unrecognized? {
        return null
    }

    class Unrecognized(val count: Int, val problem: ProblemAtIndex)

    sealed interface Success<out T> : ParsedArguments<T> {
        fun value(): T
    }

    sealed interface Failure : ParsedArguments<Nothing> {
        fun problems(): List<ProblemAtIndex>
    }
}