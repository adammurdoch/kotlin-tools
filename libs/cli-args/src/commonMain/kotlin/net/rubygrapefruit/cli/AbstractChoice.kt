package net.rubygrapefruit.cli

internal abstract class AbstractChoice<T : Any>(
    private val choices: List<ChoiceDetails<T>>,
) : NonPositional {
    protected var value: T? = null

    override fun usage(): List<FlagUsage> {
        return choices.map { FlagUsage.of(it.names, it.help) }
    }

    override fun stoppedAt(arg: String): NonPositional.StopResult {
        return if (choices.any { it.names.contains(arg) }) NonPositional.StopResult.Recognized else NonPositional.StopResult.Nothing
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
}
