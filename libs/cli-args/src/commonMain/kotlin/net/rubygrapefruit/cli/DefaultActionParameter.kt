package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionParameter<T : Action>(
    actions: ActionSet<T>,
    host: Host,
    private val owner: Action
) : AbstractActionParameter<T>(actions, host), RequiredParameter<T> {

    override fun whenAbsent(default: T): Parameter<T> {
        return owner.replace(this, OptionalActionParameter(actions, host, default))
    }

    override fun optional(): Parameter<T?> {
        return owner.replace(this, NullableActionParameter(actions, host))
    }

    override fun whenMissing(context: ParseContext): ArgParseException? {
        return if (action == null) {
            PositionalParseException("Action not provided", resolution = "Please specify an action to run.", positional = context.positional.map { it.usage() }, actions = actionInfo)
        } else {
            null
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return action ?: throw IllegalStateException()
    }
}
