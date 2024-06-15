package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultParameter(private val name: String, private val help: String?, private val host: Host, default: String?) : Positional(), Parameter<String> {
    private var value: String? = default

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value ?: throw IllegalStateException()
    }

    override fun usage(): PositionalUsage {
        return PositionalUsage("<$name>", help)
    }

    override fun accept(args: List<String>): ParseResult {
        val candidate = args.first()
        return if (host.isOption(candidate)) {
            ParseResult.Nothing
        } else {
            value = candidate
            ParseResult.One
        }
    }

    override fun missing(): ArgParseException? {
        return if (value == null) {
            ArgParseException("Parameter '$name' not provided")
        } else {
            null
        }
    }
}