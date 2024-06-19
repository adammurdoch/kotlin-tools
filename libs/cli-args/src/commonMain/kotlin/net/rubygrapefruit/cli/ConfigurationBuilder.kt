package net.rubygrapefruit.cli

/**
 * Can add options and parameters to an [Action]
 */
interface ConfigurationBuilder<T : Any> {
    /**
     * Defines an option with the given names and type <T>.
     * Can use `--<name> <value>` to specify the value.
     * For single character names, use `-<name> <value>` to specify the value.
     *
     * The option can appear anywhere in the command-line. It can appear only once.
     * Has value `null` when the option is not present in the input.
     * Use [NullableOption.whenAbsent] to use a different default.
     */
    fun option(name: String, vararg names: String, help: String? = null): NullableOption<T> {
        return option(listOf(name) + names, help = help)
    }

    fun option(names: List<String>, help: String? = null): NullableOption<T>
}