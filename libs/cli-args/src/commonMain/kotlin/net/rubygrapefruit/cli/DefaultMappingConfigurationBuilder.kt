package net.rubygrapefruit.cli

internal class DefaultMappingConfigurationBuilder<T : Any>(
    owner: Action,
    host: Host,
    converter: ChoiceConverter<T>,
    private val choices: List<ChoiceDetails<T>>
) : DefaultConfigurationBuilder<T>(owner, host, converter), MappingConfigurationBuilder<T> {
    override fun flags(): NullableOption<T> {
        val flags = choices.map { choice -> ChoiceDetails(choice.value, choice.help, choice.names.map { host.marker(it) }) }
        return owner.add(NullableChoiceFlag(ChoiceFlagMatcher(flags), owner))
    }
}
