package net.rubygrapefruit.cli

internal class DefaultActions<T : Action>(private val host: Host) : Action.Actions<T> {
    val actions = mutableMapOf<String, ChoiceDetails<T>>()

    override fun action(action: T, name: String, help: String?) {
        host.validate(name, "an action name")
        actions[name] = ChoiceDetails(action, help)
    }
}