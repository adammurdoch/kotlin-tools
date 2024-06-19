package net.rubygrapefruit.cli

internal class DefaultMappingConfigurationBuilder<T : Any>(
    owner: Action,
    host: Host,
    private val converter: ChoiceConverter<T>
) : DefaultConfigurationBuilder<T>(owner, host, converter), MappingConfigurationBuilder<T> {
    override fun flags(): NullableOption<T> {
        return owner.add(DefaultNullableChoice(converter.choices.mapKeys { DefaultHost.option(it.key) }, owner))
    }
}
