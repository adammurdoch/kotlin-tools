package net.rubygrapefruit.cli

internal class DefaultNullableIntOption(name: String, private val owner: Action) : AbstractOption<Int?>(name), NullableOption<Int> {
    override fun default(value: Int): Option<Int> {
        val option = DefaultIntOption(name, value)
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