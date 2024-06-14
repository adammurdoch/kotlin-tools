package net.rubygrapefruit.cli

internal class DefaultNullableStringOption(name: String, private val host: Host, private val owner: Action) : AbstractOption<String?>(name, host), NullableStringOption {
    override fun default(value: String): Option<String> {
        val option = DefaultStringOption(name, host, value)
        owner.replace(this, option)
        return option
    }

    override fun int(): NullableOption<Int> {
        val option = DefaultNullableIntOption(name, host, owner)
        owner.replace(this, option)
        return option
    }

    override fun convert(arg: String?): String? {
        return arg
    }
}