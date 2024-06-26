package net.rubygrapefruit.cli

internal class DefaultActions<T : Action>(private val host: Host) : Action.Actions<T> {
    val options = mutableMapOf<String, ChoiceDetails<T>>()
    val parameters = mutableMapOf<String, ChoiceDetails<T>>()

    override fun action(action: T, name: String, help: String?) {
        host.validate(name, "an action name")
        parameters[name] = ChoiceDetails(action, help)
    }

    override fun option(action: T, name: String, help: String?) {
        options[host.option(name)] = ChoiceDetails(action, help)
    }
}