package net.rubygrapefruit.cli

class ActionUsage(
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
            val parameters = positional.filterIsInstance<ParameterUsage>().filter { it.help != null }
            val actions = positional.filterIsInstance<ActionParameterUsage>().flatMap { it.actions }
            builder.appendItems("Parameters", parameters)
            builder.appendItems("Actions", actions)
            builder.appendItems("Options", options)
            return builder.toString()
        }
}

sealed class ItemUsage(val help: String?) {
    /**
     * A display name for this item, used when listing items in a table.
     */
    abstract val display: String
}

class SingleOptionUsage(val usage: String, val help: String?, val aliases: List<String>)

class OptionUsage(
    override val display: String,
    help: String?,
    val items: List<SingleOptionUsage>
) : ItemUsage(help)

sealed class PositionalUsage(
    /**
     * The usage for this item, shown in the containing action's usage summary.
     */
    val usage: String,
    override val display: String,
    help: String?,
) : ItemUsage(help) {
}

class ParameterUsage(
    usage: String,
    display: String,
    help: String?,
    val path: Boolean
) : PositionalUsage(usage, display, help) {
    constructor(usage: String, help: String?, path: Boolean) : this(usage, usage, help, path)
}

class ActionParameterUsage(
    usage: String,
    display: String,
    help: String?,
    val actions: List<SubActionUsage>
) : PositionalUsage(usage, display, help)

class SubActionUsage(val name: String, help: String?, val action: ActionUsage) : ItemUsage(help) {
    override val display: String
        get() = name
}
