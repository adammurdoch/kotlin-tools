package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionParameter<T : Action>(
    private val actions: ActionSet<T>
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
        return ActionParameterParseState(this, context, actions)
    }

    class NameUsage(val name: String) : HasPositionalUsage {
        override fun usage(): PositionalUsage {
            return LiteralUsage(name, null)
        }
    }

    private inner class AllowAnywhereOption(val name: String, val option: ActionDetails<T>) : Recoverable {
        override fun maybeRecover(context: ParseContext): ParseState {
            return if (action == option.value) {
                FinishedState
            } else {
                RecoveryState(name, option.value, this@DefaultActionParameter, context)
            }
        }
    }

    private class RecoveryState<T : Action>(val name: String, val action: T, val target: DefaultActionParameter<T>, val context: ParseContext) : ParseState {
        override fun parseNextValue(args: List<String>): ParseState.Result {
            val arg = args.first()
            return if (arg == name) {
                val nestedContext = action.nestedContext(context, target, listOf(NameUsage(name)))
                return ParseState.Continue(1, action.state(nestedContext)) {
                    target.value(action)
                }
            } else {
                ParseState.Nothing
            }
        }

        override fun endOfInput(): ParseState.FinishResult {
            return ParseState.FinishSuccess {}
        }
    }

    private object FinishedState : ParseState {
        override fun parseNextValue(args: List<String>): ParseState.Result {
            return ParseState.Success(0) {}
        }

        override fun endOfInput(): ParseState.FinishResult {
            return ParseState.FinishSuccess {}
        }
    }
}
