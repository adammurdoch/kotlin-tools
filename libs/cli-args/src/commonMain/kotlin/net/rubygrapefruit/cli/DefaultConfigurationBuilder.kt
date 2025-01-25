package net.rubygrapefruit.cli

internal open class DefaultConfigurationBuilder<T : Any>(
    protected val owner: Action,
    protected val host: Host,
    private val converter: StringConverter<T>
) : ConfigurationBuilder<T> {
    override fun option(names: List<String>, help: String?): NullableOption<T> {
        names.forEach { host.validate(it, "an option name") }
        return owner.add(DefaultNullableOption(OptionMatcher(names, help, host, converter), owner))
    }

    override fun parameter(name: String, help: String?): RequiredParameter<T> {
        host.validate(name, "a parameter name")
        return owner.add(DefaultParameter(name, help, host, owner, converter))
    }

    override fun parameters(name: String, help: String?, acceptOptions: Boolean): ListParameter<T> {
        host.validate(name, "a parameter name")
        return owner.add(DefaultListParameter(name, help, host, owner, emptyList(), false, acceptOptions, converter))
    }
}