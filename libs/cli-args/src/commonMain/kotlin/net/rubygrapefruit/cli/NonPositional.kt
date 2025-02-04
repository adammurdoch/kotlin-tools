package net.rubygrapefruit.cli

internal interface NonPositional {
    fun usage(): List<NonPositionalUsage>

    fun start(context: ParseContext): ParseState

    fun accepts(option: String): Boolean

    val inheritable: Boolean
        get() = true
}