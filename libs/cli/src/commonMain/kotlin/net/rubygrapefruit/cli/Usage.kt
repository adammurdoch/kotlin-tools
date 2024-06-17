package net.rubygrapefruit.cli

internal class ActionUsage(
    val appName: String?,
    val options: List<OptionUsage>,
    val positional: List<PositionalUsage>
) {
    val formatted: String
        get() {
            val builder = StringBuilder()
            builder.append("Usage: ")
            if (appName != null) {
                builder.append(appName)
            }
            if (options.isNotEmpty()) {
                builder.append(" [options]")
            }
            for (positional in positional) {
                builder.append(" ${positional.usage}")
            }
            builder.append("\n")
            val parameters = positional.filter { it.actions.isEmpty() && it.help != null }
            val actions = positional.flatMap { it.actions }
            builder.appendItems("Parameters", parameters)
            builder.appendItems("Actions", actions)
            builder.appendItems("Options", options)
            return builder.toString()
        }
}

internal sealed class ItemUsage(val help: String?) {
    /**
     * A display name for this item, used when listing items in a table.
     */
    abstract val display: String
}

internal class SingleOptionUsage(val usage: String, val help: String?, val aliases: List<String>)

internal class OptionUsage(
    override val display: String,
    help: String?,
    val items: List<SingleOptionUsage>
) : ItemUsage(help)

internal class PositionalUsage(
    val usage: String,
    override val display: String,
    help: String?,
    val actions: List<SubActionUsage>
) : ItemUsage(help) {
    constructor(usage: String, help: String?) : this(usage, usage, help, emptyList())
}

internal class SubActionUsage(val name: String, help: String?) : ItemUsage(help) {
    override val display: String
        get() = name
}
