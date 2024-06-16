package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class MultiValueParameter(private val name: String, private val help: String?, private val host: Host) : Positional(), Parameter<List<String>> {
    private var values: List<String>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<String> {
        return values ?: emptyList()
    }

    override fun usage(): PositionalUsage {
        return PositionalUsage("<$name>...", "<$name>", help, emptyList())
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        for (index in args.indices) {
            val arg = args[index]
            if (host.isOption(arg)) {
                values = args.subList(0, index)
                return ParseResult(index, null, true)
            }
        }
        values = args.toList()
        return ParseResult(args.size, null, true)
    }

    override fun missing(): ArgParseException? {
        return null
    }
}