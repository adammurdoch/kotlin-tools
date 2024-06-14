package net.rubygrapefruit.cli

internal class DefaultStringOption(
    name: String,
    help: String?,
    host: Host,
    private val default: String
) : AbstractOption<String>(name, help, host) {
    override fun convert(arg: String?): String {
        return arg ?: default
    }
}