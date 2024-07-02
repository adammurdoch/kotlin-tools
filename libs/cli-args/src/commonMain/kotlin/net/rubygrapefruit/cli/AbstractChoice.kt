package net.rubygrapefruit.cli

internal abstract class AbstractChoice<T : Any>(
    private val choices: Map<String, ChoiceDetails<T>>,
) : NonPositional {
    protected var value: T? = null

    override fun usage(): List<OptionUsage> {
        return choices.map { OptionUsage(it.key, it.value.help, null, listOf(SingleOptionUsage(it.key, it.value.help, listOf(it.key)))) }
    }

    override fun accepts(arg: String): Boolean {
        return choices.containsKey(arg)
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val result = choices[args[0]]
        return if (result != null) {
            value = result.value
            ParseResult.One
        } else {
            ParseResult.Nothing
        }
    }
}
