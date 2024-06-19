package net.rubygrapefruit.cli

internal class DefaultBooleanConfigurationBuilder(owner: Action, host: Host) : DefaultConfigurationBuilder<Boolean>(owner, host, BooleanConverter), BooleanConfigurationBuilder {
    override fun flag(name: String, vararg names: String, help: String?, disableOption: Boolean): Flag {
        val allNames = listOf(name) + names.toList()
        allNames.forEach { DefaultHost.validate(it, "a flag name") }

        return owner.add(DefaultFlag(allNames, disableOption, help, host, false))
    }
}