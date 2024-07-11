package net.rubygrapefruit.cli

internal sealed interface ParseContext {
    val positional: List<PositionalUsage>

    val options: List<NonPositional>

    fun withOptions(options: List<NonPositional>): ParseContext

    fun replace(positional: Positional, replacement: List<HasPositionalUsage>): ParseContext
}

internal class DefaultContext(
    private val items: List<HasPositionalUsage>,
    override val options: List<NonPositional>
) : ParseContext {
    override val positional: List<PositionalUsage>
        get() = items.map { it.usage() }

    override fun withOptions(options: List<NonPositional>): ParseContext {
        return DefaultContext(items, this.options + options)
    }

    override fun replace(positional: Positional, replacement: List<HasPositionalUsage>): ParseContext {
        val index = items.indexOf(positional)
        val newPositional = items.toMutableList()
        newPositional.removeAt(index)
        newPositional.addAll(index, replacement)
        return DefaultContext(newPositional, options)
    }
}
