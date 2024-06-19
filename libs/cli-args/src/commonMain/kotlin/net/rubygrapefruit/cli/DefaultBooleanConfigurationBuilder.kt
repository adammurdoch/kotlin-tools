package net.rubygrapefruit.cli

internal class DefaultBooleanConfigurationBuilder(owner: Action, host: Host) : DefaultConfigurationBuilder<Boolean>(owner, host, BooleanConverter), BooleanConfigurationBuilder {
    override fun flag(name: String, vararg names: String, help: String?): Flag {
        val allNames = listOf(name) + names.toList()
        allNames.forEach { DefaultHost.validate(it, "a flag name") }

        return owner.add(DefaultFlag(allNames, true, help, host, false, owner))
    }
}