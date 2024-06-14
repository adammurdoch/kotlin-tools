package net.rubygrapefruit.cli

internal class DefaultStringOption(name: String, host: Host, private val default: String) : AbstractOption<String>(name, host) {
    override fun convert(arg: String?): String {
        return arg ?: default
    }
}