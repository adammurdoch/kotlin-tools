package net.rubygrapefruit.cli

import kotlin.reflect.KClass

class ActionUsage(
    val options: List<OptionUsage>,
    val positional: List<PositionalUsage>
) {
    val formatted: String
        get() {
            val first = positional.firstOrNull()
            val effective = when (first) {
                null, is ParameterUsage -> this
                is ActionParameterUsage -> {
                    val additionalOptions =
                        first.options.filter { it.name.startsWith("-") }.map { OptionUsage(it.name, it.help, null, listOf(SingleOptionUsage(it.name, it.help, emptyList()))) }
                    if (additionalOptions.size == first.options.size) {
                        // replace first positional with its default if it only uses options
                        if (first.default != null) {
                            ActionUsage(options + additionalOptions + first.default.action.options, first.default.action.positional + positional.drop(1))
                        } else {
                            ActionUsage(options + additionalOptions, positional.drop(1))
                        }
                    } else {
                        ActionUsage(options + additionalOptions, positional)
                    }
                }
            }


            val builder = StringBuilder()
            if (effective.options.isNotEmpty()) {
                builder.append("[options]")
            }
            for (positional in effective.positional) {
                if (builder.isNotEmpty()) {
                    builder.append(" ")
                }
                builder.append(positional.usage)
            }
            builder.append("\n")
            val parameters = effective.positional.filterIsInstance<ParameterUsage>().filter { it.help != null }
            val actionParameters = effective.positional.filterIsInstance<ActionParameterUsage>()
            val actions = actionParameters.flatMap { action -> action.actions() }.filter { !it.name.startsWith("-") }
            builder.appendItems("Parameters", parameters)
            builder.appendItems("Actions", actions)
            builder.appendItems("Options", effective.options)
            return builder.toString()
        }

    private fun ActionParameterUsage.actions(): List<SubActionUsage> {
        return options + named + if (default != null) default.action.actions() else emptyList()
    }

    private fun ActionUsage.actions(): List<SubActionUsage> {
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
    val options: List<SubActionUsage>,
    val named: List<SubActionUsage>,
    val default: SubActionUsage?
) : PositionalUsage(usage, display, help)

class SubActionUsage(val name: String, help: String?, val action: ActionUsage) : ItemUsage(help) {
    override val display: String
        get() = name
}
