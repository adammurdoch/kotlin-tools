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

    override fun parameter(name: String, help: String?): Parameter<T> {
        return owner.add(DefaultParameter(name, help, null, host, owner, converter))
    }

    override fun parameters(name: String, help: String?): ListParameter<T> {
        return owner.add(DefaultListParameter(name, help, host, owner, emptyList(), false, converter))
    }
}