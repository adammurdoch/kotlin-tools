package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultActionParameter<T : Action>(
    actions: ActionSet<T>,
    host: Host
) : AbstractActionParameter<T>(actions, host), Parameter<T> {

    override fun whenMissing(context: ParseContext): FinishResult {
        val exception = PositionalParseException("Action not provided", resolution = "Please specify an action to run.", positional = context.positional, actions = actionInfo)
        return FinishResult.Failure(exception, expectedMore = true)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return selected ?: throw IllegalStateException()
    }
}
