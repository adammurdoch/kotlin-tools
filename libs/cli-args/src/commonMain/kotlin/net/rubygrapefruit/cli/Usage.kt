package net.rubygrapefruit.cli

import kotlin.reflect.KClass

class ActionUsage(
    val options: List<OptionUsage>,
    val positional: List<PositionalUsage>
) {
    /**
     * Simplifies this usage by inlining nested action sets, etc.
     */
    fun effective(): ActionUsage {
        val first = positional.firstOrNull()
        return when (first) {
            null, is ParameterUsage -> this
            is ActionParameterUsage -> {
                val additionalOptions = first.options.map { OptionUsage(it.name, it.help, null, listOf(SingleOptionUsage(it.name, it.help, listOf(it.name)))) }
                val allOptions = options + additionalOptions + if (first.default != null) first.default.action.options else emptyList()
                if (first.named.isEmpty()) {
                    if (first.default != null) {
                        // replace action parameter with its default
                        ActionUsage(allOptions, first.default.action.positional + positional.drop(1))
                    } else {
                        // discard action parameter
                        ActionUsage(allOptions, positional.drop(1))
                    }
                } else {
                    // discard options from action parameter
                    ActionUsage(allOptions, listOf(first.dropOptions()) + positional.drop(1))
                }
            }
        }
    }

    val formatted: String
        get() {
            val builder = StringBuilder()
            if (options.isNotEmpty()) {
                builder.append("[options]")
            }
            for (positional in positional) {
                if (builder.isNotEmpty()) {
                    builder.append(" ")
                }
                builder.append(positional.usage)
            }
            builder.append("\n")
            val parameters = positional.filterIsInstance<ParameterUsage>().filter { it.help != null }
            val first = positional.firstOrNull()
            val actions = if (first is ActionParameterUsage) {
                first.actions
            } else {
                emptyList()
            }
            builder.appendItems("Parameters", parameters)
            builder.appendItems("Actions", actions)
            builder.appendItems("Options", options)
            return builder.toString()
        }

    private fun ActionParameterUsage.actions(): List<NamedNestedActionUsage> {
        return options + named + if (default != null) default.action.actions() else emptyList()
    }

    private fun ActionUsage.actions(): List<NamedNestedActionUsage> {
        val first = positional.firstOrNull()
        return if (first is ActionParameterUsage) {
            first.actions()
        } else {
            emptyList()
        }
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
    /**
     * The argument type.
     */
    val type: KClass<*>?,
    val usages: List<SingleOptionUsage>
) : ItemUsage(help)

sealed class Cardinality {
    data object Optional : Cardinality()
    data object Required : Cardinality()
    data object ZeroOrMore : Cardinality()
    data object OneOrMore : Cardinality()
}

sealed class PositionalUsage(
    /**
     * The usage for this item, shown in the containing action's usage summary.
     */
    val usage: String,
    override val display: String,
    help: String?,
) : ItemUsage(help)

class ParameterUsage(
    usage: String,
    display: String,
    help: String?,
    val type: KClass<*>,
    val cardinality: Cardinality,
    val values: List<String> = emptyList()
) : PositionalUsage(usage, display, help) {
    val multiple: Boolean
        get() = cardinality == Cardinality.ZeroOrMore || cardinality == Cardinality.OneOrMore
}

class ActionParameterUsage(
    usage: String,
    display: String,
    help: String?,
    val options: List<NamedNestedActionUsage>,
    val named: List<NamedNestedActionUsage>,
    val default: DefaultNestedActionUsage?
) : PositionalUsage(usage, display, help) {

    fun dropOptions(): ActionParameterUsage {
        return ActionParameterUsage(usage, display, help, emptyList(), named, default)
    }

    val actions: List<NestedActionUsage>
        get() = options + named + if (default != null) listOf(default) else emptyList()
}

sealed class NestedActionUsage(help: String?, val action: ActionUsage) : ItemUsage(help)

class NamedNestedActionUsage(val name: String, help: String?, action: ActionUsage) : NestedActionUsage(help, action) {
    override val display: String
        get() = name
}

class DefaultNestedActionUsage(help: String?, action: ActionUsage) : NestedActionUsage(help, action) {
    override val display: String
        get() = action.positional.joinToString(" ") { it.usage }
}