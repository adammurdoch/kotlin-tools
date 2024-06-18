package net.rubygrapefruit.cli

internal class DefaultNullableStringOption(
    names: List<String>,
    help: String?,
    private val host: Host,
    private val owner: Action
) : DefaultNullableOption<String>(names, help, host, owner, NoOpConverter), NullableStringOption {

    override fun int(): NullableOption<Int> {
        val option = DefaultNullableOption(names, help, host, owner, IntConverter)
        owner.replace(this, option)
        return option
    }
}