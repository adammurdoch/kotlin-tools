package net.rubygrapefruit.cli

internal class DefaultNullableStringOption(
    names: List<String>,
    help: String?,
    private val host: Host,
    private val owner: Action
) : DefaultNullableOption<String>(names, help, host, owner, NoOpConverter), NullableStringOption {

    override fun int(): NullableOption<Int> {
        return owner.replace(this, DefaultNullableOption(names, help, host, owner, IntConverter))
    }
}