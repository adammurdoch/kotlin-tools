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
        /**
         * Called after [claimUnrecognized]
         */
        fun problems(): List<ProblemAtIndex>
    }

    class FixedResult<out T>(val value: T) : Success<T> {
        override fun value(): T {
            return value
        }
    }

    class FixedProblems(val problems: List<ProblemAtIndex>) : Failure {
        override fun problems(): List<ProblemAtIndex> {
            return problems
        }
    }
}