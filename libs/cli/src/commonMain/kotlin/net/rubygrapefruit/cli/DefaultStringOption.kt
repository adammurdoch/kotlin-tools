package net.rubygrapefruit.cli

internal class DefaultStringOption(
    names: List<String>,
    help: String?,
    host: Host,
    private val default: String
) : AbstractOption<String>(names, help, host) {
    override fun convert(flag: String, arg: String?): String {
        return arg ?: default
    }
}