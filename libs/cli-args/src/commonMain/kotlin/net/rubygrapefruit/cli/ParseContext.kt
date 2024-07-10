package net.rubygrapefruit.cli

internal sealed interface ParseContext {
    val prefix: List<PositionalUsage>

    val options: List<NonPositional>

    fun withOptions(options: List<NonPositional>): ParseContext
}

internal data object RootContext : ParseContext {
    override val prefix: List<PositionalUsage>
        get() = emptyList()

    override val options: List<NonPositional>
        get() = emptyList()

    override fun withOptions(options: List<NonPositional>): ParseContext {
        return NestedContext(this, options)
    }
}

internal class NestedContext(private val parent: ParseContext, override val options: List<NonPositional>) : ParseContext {
    override val prefix: List<PositionalUsage>
        get() = parent.prefix

    override fun withOptions(options: List<NonPositional>): ParseContext {
        return NestedContext(this, options + this.options)
    }
}