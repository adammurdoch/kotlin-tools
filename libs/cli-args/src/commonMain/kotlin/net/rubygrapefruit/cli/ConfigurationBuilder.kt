package net.rubygrapefruit.cli

/**
 * Can add parameters to an [Action]
 */
interface ConfigurationBuilder<T : Any> {
    /**
     * Defines an option with the given names and type [T].
     * Can use `--<name> <value>` to specify the value.
     * For single character names, use `-<name> <value>` to specify the value.
     *
     * The option can appear anywhere in the command-line. It can appear only once.
     * Has value `null` when the option is not present in the input.
     * Use [NullableOption.whenAbsent] to use a different default.
     * Use [NullableOption.required] to require the option to be present.
     */
    fun option(name: String, vararg names: String, help: String? = null): NullableOption<T> {
        return option(listOf(name) + names, help = help)
    }

    /**
     * Defines an option with the given names and type [T].
     * Can use `--<name> <value>` to specify the value.
     * For single character names, use `-<name> <value>` to specify the value.
     *
     * The option can appear anywhere in the command-line. It can appear only once.
     * Has value `null` when the option is not present in the input.
     * Use [NullableOption.whenAbsent] to use a different default.
     * Use [NullableOption.required] to require the option to be present.
     */
    fun option(names: List<String>, help: String? = null): NullableOption<T>

    /**
     * Defines a parameter with the given name and type [T].
     *
     * The parameter must appear at a specific location in the input.
     * Fails if the parameter is not present. Use [RequiredParameter.whenAbsent] or [RequiredParameter.optional] to allow the parameter to be missing.
     */
    fun parameter(name: String, help: String? = null): RequiredParameter<T>
}

interface MappingConfigurationBuilder<T : Any> : ConfigurationBuilder<T> {
    /**
     * Defines a set of flags to select one of the values.
     *
     * The flags can appear anywhere in the input. They can be specified multiple times and the last value is used.
     * Has value `null` then none of the flags is present in the input. Use [NullableOption.whenAbsent] to use a different default.
     * Use [NullableOption.required] to require one of the flags to be present.
     *
     */
    fun flags(): NullableOption<T>
}

interface BooleanConfigurationBuilder : ConfigurationBuilder<Boolean> {
    /**
     * Defines a boolean flag with the given names. Can use `--<name>` or `--no-<name>` to specify the value.
     * For single character names, use `-<name>` to specify the value.
     *
     * The flag can appear anywhere in the command-line. It can be specified multiple times and the last value is used.
     * Has value `false` when the flag is not present in the input.
     */
    fun flag(name: String, vararg names: String, help: String? = null, disableOption: Boolean = true): Flag
}