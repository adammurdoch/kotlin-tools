package net.rubygrapefruit.cli

internal class DefaultNullableIntOption(
    names: List<String>,
    help: String?,
    private val host: Host,
    private val owner: Action
) : AbstractOption<Int?>(names, help, host), NullableOption<Int> {
    override fun whenAbsent(value: Int): Option<Int> {
        val option = DefaultIntOption(names, help, host, value)
        owner.replace(this, option)
        return option
    }

    override fun convert(flag: String, arg: String?): Int? {
        return if (arg == null) {
            null
        } else {
            arg.toIntOrNull() ?: throw ArgParseException("Argument for option $flag is not an integer: $arg")
        }
    }
}