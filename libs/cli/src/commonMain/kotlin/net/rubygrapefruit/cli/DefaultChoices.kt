package net.rubygrapefruit.cli

internal class DefaultChoices<T>(
    private val host: Host
) : Action.Choices<T> {
    val choices = mutableMapOf<String, ChoiceDetails<T>>()

    override fun choice(value: T, name: String, vararg names: String, help: String?) {
        val details = ChoiceDetails(value, help)
        val allNames = listOf(name) + names
        allNames.iterator().forEach {
            host.validate(it, "an option name")
            choices[it] = details
        }
    }
}