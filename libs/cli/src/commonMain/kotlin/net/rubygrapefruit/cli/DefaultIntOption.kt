package net.rubygrapefruit.cli

internal class DefaultIntOption(name: String, val default: Int) : AbstractOption<Int>(name), Option<Int> {
    override fun convert(arg: String?): Int {
        return if (arg == null) {
            default
        } else {
            arg.toIntOrNull() ?: throw ArgParseException("Argument for option $flag is not an integer: $arg")
        }
    }
}