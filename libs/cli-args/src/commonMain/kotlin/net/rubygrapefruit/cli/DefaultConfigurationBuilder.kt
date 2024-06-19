package net.rubygrapefruit.cli

internal class DefaultConfigurationBuilder<T : Any>(
    private val owner: Action,
    private val host: Host,
    private val converter: StringConverter<T>
) : ConfigurationBuilder<T> {
    override fun option(names: List<String>, help: String?): NullableOption<T> {
        names.forEach { host.validate(it, "an option name") }
        return owner.add(DefaultNullableOption(names, help, host, owner, converter))
    }
}