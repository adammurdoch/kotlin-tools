package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultArgument(private val name: String, private val host: Host, default: String?) : Positional(), Argument<String> {
    private var value: String? = default

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value ?: throw IllegalStateException()
    }

    override fun usage(): PositionalUsage {
        return PositionalUsage("<$name>")
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
            ArgParseException("Argument '$name' not provided")
        } else {
            null
        }
    }
}