package net.rubygrapefruit.cli

internal class DefaultNullableIntOption(name: String) : AbstractOption<Int?>(name) {
    override fun convert(arg: String?): Int? {
        println("CONVERT $arg")
        return if (arg == null) {
            null
        } else {
            arg.toIntOrNull() ?: throw ArgParseException("Argument for option $flag is not an integer: $arg")
        }
    }
}