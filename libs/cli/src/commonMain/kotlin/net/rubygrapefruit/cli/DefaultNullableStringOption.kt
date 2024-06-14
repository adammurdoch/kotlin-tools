package net.rubygrapefruit.cli

internal class DefaultNullableStringOption(
    name: String,
    help: String?,
    private val host: Host,
    private val owner: Action
) : AbstractOption<String?>(name, help, host), NullableStringOption {
    override fun default(value: String): Option<String> {
        val option = DefaultStringOption(name, help, host, value)
        owner.replace(this, option)
        return option
    }

    override fun int(): NullableOption<Int> {
        val option = DefaultNullableIntOption(name, help, host, owner)
        owner.replace(this, option)
        return option
    }

    override fun convert(arg: String?): String? {
        return arg
    }
}