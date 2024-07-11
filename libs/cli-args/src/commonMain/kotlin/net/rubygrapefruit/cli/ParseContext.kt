package net.rubygrapefruit.cli

internal sealed interface ParseContext {
    val positional: List<HasPositionalUsage>

    val options: List<NonPositional>

    fun withOptions(options: List<NonPositional>): ParseContext

    fun replace(positional: Positional, replacement: List<HasPositionalUsage>): ParseContext
}

internal class DefaultContext(
    override val positional: List<HasPositionalUsage>,
    override val options: List<NonPositional>
) : ParseContext {
    override fun withOptions(options: List<NonPositional>): ParseContext {
        return DefaultContext(positional, this.options + options)
    }

    override fun replace(positional: Positional, replacement: List<HasPositionalUsage>): ParseContext {
        val index = this.positional.indexOf(positional)
        val newPositional = this.positional.toMutableList()
        newPositional.removeAt(index)
        newPositional.addAll(index, replacement)
        return DefaultContext(newPositional, options)
    }
}
