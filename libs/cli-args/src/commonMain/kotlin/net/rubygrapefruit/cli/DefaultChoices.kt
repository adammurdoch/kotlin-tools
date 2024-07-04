package net.rubygrapefruit.cli

internal class DefaultChoices<T>(
    private val host: Host
) : Action.Choices<T> {
    val choices = mutableListOf<ChoiceDetails<T>>()

    override fun choice(value: T, name: String, vararg names: String, help: String?) {
        val allNames = listOf(name) + names
        val details = ChoiceDetails(value, help, allNames)
        allNames.iterator().forEach {
            host.validate(it, "a choice name")
        }
        choices.add(details)
    }
}