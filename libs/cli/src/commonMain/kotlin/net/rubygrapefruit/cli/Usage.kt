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
            val positionalWithHelp = positional.filter { it.help != null }
            if (positionalWithHelp.isNotEmpty()) {
                builder.append("\nParameters:\n")
                builder.appendItems(positionalWithHelp)
                builder.append("\n")
            }
            if (options.isNotEmpty()) {
                builder.append("\nOptions:\n")
                builder.appendItems(options)
                builder.append("\n")
            }
            return builder.toString()
        }
}

internal sealed class ItemUsage(val help: String?) {
    abstract val display: String
}

internal class OptionUsage(
    val usage: String,
    help: String?
) : ItemUsage(help) {
    override val display: String
        get() = usage
}

internal class PositionalUsage(
    val usage: String,
    override val display: String,
    help: String?
) : ItemUsage(help) {
    constructor(usage: String, help: String?) : this(usage, usage, help)
}
