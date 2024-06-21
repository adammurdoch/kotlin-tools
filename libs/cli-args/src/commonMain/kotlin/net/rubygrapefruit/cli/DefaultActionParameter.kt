package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionParameter<T : Action>(
    actions: Map<String, ChoiceDetails<T>>,
    host: Host,
    private val owner: Action
) : AbstractActionParameter<T>(actions, host), RequiredParameter<T> {

    override fun whenAbsent(default: T): Parameter<T> {
        return owner.replace(this, OptionalActionParameter(actions, host, default))
    }

    override fun optional(): Parameter<T?> {
        return owner.replace(this, NullableActionParameter(actions, host))
    }

    override fun missing(): ArgParseException? {
        return ArgParseException("Action not provided", resolution = "Please specify an action to run.", actions = actionInfo)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return action ?: throw IllegalStateException()
    }
}
