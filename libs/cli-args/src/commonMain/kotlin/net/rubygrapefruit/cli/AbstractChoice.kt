package net.rubygrapefruit.cli

internal abstract class AbstractChoice<T : Any>(
    protected val choices: List<ChoiceDetails<T>>,
) : NonPositional {
    protected var value: T? = null

    override fun usage(): List<FlagUsage> {
        return choices.map { FlagUsage.of(it.names, it.help) }
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val name = args.first()
        val result = choices.firstOrNull { it.names.contains(name) }
        return if (result != null) {
            value = result.value
            ParseResult.One
        } else {
            ParseResult.Nothing
        }
    }

    override fun accepts(option: String): Boolean {
        return choices.any { it.names.contains(option) }
    }
}
