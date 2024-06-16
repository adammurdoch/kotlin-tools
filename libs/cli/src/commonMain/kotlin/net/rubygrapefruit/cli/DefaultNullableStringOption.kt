package net.rubygrapefruit.cli

internal class DefaultNullableStringOption(
    names: List<String>,
    help: String?,
    private val host: Host,
    private val owner: Action
) : AbstractOption<String?>(names, help, host), NullableStringOption {
    override fun whenAbsent(default: String): Option<String> {
        val option = DefaultStringOption(names, help, host, default)
        owner.replace(this, option)
        return option
    }

    override fun int(): NullableOption<Int> {
        val option = DefaultNullableIntOption(names, help, host, owner)
        owner.replace(this, option)
        return option
    }

    override fun convert(flag: String, arg: String?): String? {
        return arg
    }
}