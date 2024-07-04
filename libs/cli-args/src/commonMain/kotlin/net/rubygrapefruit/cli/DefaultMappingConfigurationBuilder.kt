package net.rubygrapefruit.cli

internal class DefaultMappingConfigurationBuilder<T : Any>(
    owner: Action,
    host: Host,
    converter: ChoiceConverter<T>,
    private val choices: List<ChoiceDetails<T>>
) : DefaultConfigurationBuilder<T>(owner, host, converter), MappingConfigurationBuilder<T> {
    override fun flags(): NullableOption<T> {
        return owner.add(DefaultNullableChoice(choices.map { choice -> ChoiceDetails(choice.value, choice.help, choice.names.map { host.option(it) }) }, owner))
    }
}
