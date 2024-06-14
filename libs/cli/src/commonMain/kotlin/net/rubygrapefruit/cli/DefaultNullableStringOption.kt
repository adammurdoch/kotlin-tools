package net.rubygrapefruit.cli

internal class DefaultNullableStringOption(name: String, private val owner: Action) : AbstractOption<String?>(name), NullableStringOption {
    override fun default(value: String): Option<String> {
        val option = DefaultStringOption(name, value)
        owner.replace(this, option)
        return option
    }

    override fun int(): Option<Int?> {
        val option = DefaultNullableIntOption(name)
        owner.replace(this, option)
        return option
    }

    override fun convert(arg: String?): String? {
        return arg
    }
}