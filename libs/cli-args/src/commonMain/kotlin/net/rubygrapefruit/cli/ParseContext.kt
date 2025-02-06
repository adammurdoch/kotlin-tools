package net.rubygrapefruit.cli

internal sealed interface ParseContext {
    val positional: List<PositionalUsage>

    fun withOptions(options: List<NonPositional>): ParseContext

    fun nested(positional: Positional, replacement: List<HasPositionalUsage>): ParseContext

    fun isOption(flag: String): Boolean
}

internal class DefaultContext(
    private val host: Host,
    private val items: List<HasPositionalUsage>
) : ParseContext {
    override val positional: List<PositionalUsage>
        get() = items.map { it.usage() }

    override fun isOption(flag: String): Boolean {
        return host.isOption(flag)
    }

    override fun withOptions(options: List<NonPositional>): ParseContext {
        return DefaultContext(host, items)
    }

    override fun nested(positional: Positional, replacement: List<HasPositionalUsage>): ParseContext {
        val index = items.indexOf(positional)
        val newPositional = items.toMutableList()
        newPositional.removeAt(index)
        newPositional.addAll(index, replacement)
        return DefaultContext(host, newPositional)
    }
}
