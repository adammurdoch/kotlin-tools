package net.rubygrapefruit.cli

internal class DefaultStringParameter(
    name: String,
    help: String?,
    default: String?,
    host: Host,
    owner: Action
) : DefaultParameter<String>(name, help, default, host, owner, NoOpConverter), StringParameter<String> {
    override fun int(): Parameter<Int> {
        return owner.replace(this, DefaultParameter(name, help, null, host, owner, IntConverter))
    }
}