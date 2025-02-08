package net.rubygrapefruit.cli

internal class DefaultActions<T : Action>(private val host: Host) : Action.Actions<T> {
    private val options = mutableMapOf<String, ActionDetails<T>>()
    private val parameters = mutableMapOf<String, ActionDetails<T>>()
    private var default: ActionDetails<T>? = null

    override fun action(action: T, name: String, help: String?) {
        host.validate(name, "an action name")
        parameters[name] = ActionDetails(action, help, false)
    }

    override fun action(action: T, help: String?) {
        default = ActionDetails(action, help, false)
    }

    override fun option(action: T, name: String, help: String?, allowAnywhere: Boolean) {
        options[host.marker(name)] = ActionDetails(action, help, allowAnywhere)
    }

    fun build() = ActionSet(options.toMap(), parameters.toMap(), default)
}