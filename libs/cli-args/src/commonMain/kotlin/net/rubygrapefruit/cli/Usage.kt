package net.rubygrapefruit.cli

import kotlin.reflect.KClass

data class ActionUsage(
    val options: List<OptionUsage>,
    val positional: List<PositionalUsage>
) {
    /**
     * Simplifies this usage by inlining the first action set and replacing its option actions with options on this action.
     */
    fun effective(): ActionUsage {
        val firstPositional = positional.firstOrNull()
        return when (firstPositional) {
            null, is ParameterUsage -> this
            is ActionParameterUsage -> {
                // Replace option actions from first positional with options on this action
                // If there are no named actions in first positional, can replace it with its default
                val effective = firstPositional.inlineActions()
                val firstPositionalOptions = effective.options.map { OptionUsage.of(it.name, it.help) }
                val allOptions = options + firstPositionalOptions + if (effective.default != null) effective.default.action.options else emptyList()
                if (effective.named.isEmpty()) {
                    if (effective.default != null) {
                        // replace action parameter with its default
                        ActionUsage(allOptions, effective.default.action.positional + positional.drop(1))
                    } else {
                        // discard action parameter
                        ActionUsage(allOptions, positional.drop(1))
                    }
                } else {
                    // discard options from action parameter
                    ActionUsage(allOptions, listOf(effective.dropOptions()) + positional.drop(1))
                }
            }
        }
    }

    fun inlineActions(): ActionUsage {
        return ActionUsage(options, positional.map { if (it is ActionParameterUsage) it.inlineActions() else it })
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

    fun dropOptions(): ActionUsage {
        return ActionUsage(emptyList(), positional)
    }
}

class SingleOptionUsage(val usage: String, val help: String?, val aliases: List<String>)

class OptionUsage(
    val usage: String,
    val help: String?,
    /**
     * The argument type, if known.
     */
    val type: KClass<*>?,
    val choices: List<SingleOptionUsage>
) {
    companion object {
        fun of(names: List<String>, help: String?): OptionUsage {
            val usage = names.joinToString(", ")
            return OptionUsage(usage, help, null, listOf(SingleOptionUsage(usage, help, names)))
        }

        fun of(name: String, help: String?): OptionUsage {
            return OptionUsage(name, help, null, listOf(SingleOptionUsage(name, help, listOf(name))))
        }
    }
}

sealed class Cardinality(val multiple: Boolean) {
    data object Optional : Cardinality(false)
    data object Required : Cardinality(false)
    data object ZeroOrMore : Cardinality(true)
    data object OneOrMore : Cardinality(true)
}

sealed class PositionalUsage(
    /**
     * The usage for this item, shown in the containing action's usage summary.
     */
    val usage: String,
    val display: String,
    val help: String?,
)

class ParameterUsage(
    usage: String,
    display: String,
    help: String?,
    val type: KClass<*>,
    val cardinality: Cardinality,
    val values: List<String> = emptyList()
) : PositionalUsage(usage, display, help)

class ActionParameterUsage(
    usage: String,
    display: String,
    help: String?,
    val options: List<NamedNestedActionUsage>,
    val named: List<NamedNestedActionUsage>,
    val default: DefaultNestedActionUsage?
) : PositionalUsage(usage, display, help) {

    override fun toString(): String {
        return "ActionParameterUsage($options, $named, $default)"
    }

    fun dropOptions(): ActionParameterUsage {
        return ActionParameterUsage(usage, display, help, emptyList(), named, default)
    }

    /**
     * Simplifies this action set by inlining nested actions from the default action, if any.
     */
    fun inlineActions(): ActionParameterUsage {
        if (default != null) {
            val inlinedDefault = default.inlineActions()
            val firstPositional = inlinedDefault.action.positional.firstOrNull()
            if (firstPositional is ActionParameterUsage) {
                return ActionParameterUsage(usage, display, help, options + firstPositional.options, named + firstPositional.named, null)
            }
        }
        return this
    }

    val actions: List<NestedActionUsage>
        get() = options + named + if (default != null) listOf(default) else emptyList()
}

sealed class NestedActionUsage(val help: String?, val action: ActionUsage) {
    abstract val display: String
}

class NamedNestedActionUsage(val name: String, help: String?, action: ActionUsage) : NestedActionUsage(help, action) {
    override val display: String
        get() = name
}

class DefaultNestedActionUsage(help: String?, action: ActionUsage) : NestedActionUsage(help, action) {
    override val display: String
        get() = action.positional.joinToString(" ") { it.usage }

    fun inlineActions(): DefaultNestedActionUsage {
        return DefaultNestedActionUsage(help, action.inlineActions())
    }
}