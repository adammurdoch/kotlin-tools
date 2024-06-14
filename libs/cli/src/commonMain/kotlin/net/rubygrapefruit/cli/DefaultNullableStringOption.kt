package net.rubygrapefruit.cli

internal class DefaultNullableStringOption(name: String) : AbstractOption<String?>(name), NullableStringOption {
    override fun default(value: String): Option<String> {
        return DefaultStringOption(name, value)
    }

    override fun convert(arg: String?): String? {
        return arg
    }
}