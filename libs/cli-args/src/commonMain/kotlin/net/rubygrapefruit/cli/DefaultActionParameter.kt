package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionParameter<T : Action>(
    private val actions: ActionSet<T>,
    private val host: Host
) : Positional, Parameter<T> {
    private var action: T? = null

    val actionInfo
        get() = actions.named.map { NamedNestedActionUsage(it.key, it.value.help, it.value.value.usage()) }

    val recoverables: List<Recoverable> = actions.options.mapNotNull { if (it.value.allowAnywhere) AllowAnywhereOption(it.key, it.value) else null }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return action ?: throw IllegalStateException()
    }

    override fun usage(): PositionalUsage {
        val optionInfo = actions.options.map { NamedNestedActionUsage(it.key, it.value.help, it.value.value.usage()) }
        val defaultInfo = actions.default?.let { DefaultNestedActionUsage(it.help, it.value.usage()) }
        return ActionParameterUsage("<action>", "<action>", null, optionInfo, actionInfo, defaultInfo)
    }

    override fun usage(name: String): ActionUsage? {
        val action = actions.named[name]
        if (action != null) {
            return action.value.usage()
        }
        if (actions.default != null) {
            return actions.default.value.usage(name)
        }

        return null
    }

    fun value(action: T) {
        this.action = action
    }

    override fun start(context: ParseContext): ParseState {
        return ActionParameterParseState(this, context, actions, host)
    }

    class NameUsage(val name: String) : HasPositionalUsage {
        override fun usage(): PositionalUsage {
            return LiteralUsage(name, null)
        }
    }

    private inner class AllowAnywhereOption(val name: String, val option: ActionDetails<T>) : Recoverable {

        override fun toString(): String {
            return name
        }

        override fun maybeRecover(args: List<String>, context: ParseContext): Boolean {
            return if (action == option.value) {
                true
            } else if (args.firstOrNull() == this.name) {
                action = option.value
                val result = option.value.maybeParse(args.drop(1), context)
                result.failure == null
            } else {
                false
            }
        }
    }
}
