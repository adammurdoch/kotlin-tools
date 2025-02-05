package net.rubygrapefruit.cli

internal interface FailureHint {
    fun map(args: List<String>): ParseState.Failure?
}