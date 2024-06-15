package net.rubygrapefruit.cli

internal class DefaultIntOption(
    names: List<String>,
    help: String?,
    host: Host,
    val default: Int
) : AbstractOption<Int>(names, help, host), Option<Int> {
    override fun convert(flag: String, arg: String?): Int {
        return if (arg == null) {
            default
        } else {
            arg.toIntOrNull() ?: throw ArgParseException("Argument for option $flag is not an integer: $arg")
        }
    }
}