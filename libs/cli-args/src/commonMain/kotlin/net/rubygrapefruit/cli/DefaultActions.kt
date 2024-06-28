package net.rubygrapefruit.cli

internal class DefaultActions<T : Action>(private val host: Host) : Action.Actions<T> {
    private val options = mutableMapOf<String, ChoiceDetails<T>>()
    private val parameters = mutableMapOf<String, ChoiceDetails<T>>()
    private var default: ChoiceDetails<T>? = null

    override fun action(action: T, name: String, help: String?) {
        host.validate(name, "an action name")
        parameters[name] = ChoiceDetails(action, help)
    }

    override fun action(action: T, help: String?) {
        default = ChoiceDetails(action, help)
    }

    override fun option(action: T, name: String, help: String?) {
        options[host.option(name)] = ChoiceDetails(action, help)
    }

    fun build() = ActionSet(options.toMap(), parameters.toMap(), default)
}