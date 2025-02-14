package net.rubygrapefruit.cli

internal class ChoiceFlagMatcher<T : Any>(
    val choices: List<ChoiceDetails<T>>
) : Matcher<T> {
    override val markers: List<String>
        get() = choices.flatMap { it.names }

    override fun match(args: List<String>): Matcher.Result<T> {
        val name = args.first()
        val result = choices.firstOrNull { it.names.contains(name) }
        return if (result != null) {
            Matcher.Success(1, result.value)
        } else {
            Matcher.Nothing()
        }
    }

    override fun usage(): List<FlagUsage> {
        return choices.map { FlagUsage.of(it.names, it.help) }
    }
}