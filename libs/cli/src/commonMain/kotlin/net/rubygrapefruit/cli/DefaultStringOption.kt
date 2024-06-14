package net.rubygrapefruit.cli

internal class DefaultStringOption(name: String, private val default: String) : AbstractOption<String>(name) {
    override fun convert(arg: String?): String {
        return arg ?: default
    }
}