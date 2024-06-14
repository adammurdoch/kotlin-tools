package net.rubygrapefruit.cli

internal class DefaultNullableIntOption(name: String, private val host: Host, private val owner: Action) : AbstractOption<Int?>(name, host), NullableOption<Int> {
    override fun default(value: Int): Option<Int> {
        val option = DefaultIntOption(name, host, value)
        owner.replace(this, option)
        return option
    }

    override fun convert(arg: String?): Int? {
        return if (arg == null) {
            null
        } else {
            arg.toIntOrNull() ?: throw ArgParseException("Argument for option $flag is not an integer: $arg")
        }
    }
}