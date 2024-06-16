package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultParameter(
    private val name: String,
    private val help: String?,
    private val host: Host,
    private val owner: Action,
    private val default: String?
) : Positional(), Parameter<String> {
    private var value: String? = null

    override fun whenAbsent(default: String): Parameter<String> {
        val parameter = DefaultParameter(name, help, host, owner, default)
        owner.replace(this, parameter)
        return parameter
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return (value ?: default) ?: throw IllegalStateException()
    }

    override fun usage(): PositionalUsage {
        return PositionalUsage("<$name>", help)
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val candidate = args.first()
        return if (host.isOption(candidate)) {
            ParseResult.Nothing
        } else {
            value = candidate
            ParseResult.One
        }
    }

    override fun missing(): ArgParseException? {
        return if (default == null) {
            ArgParseException("Parameter '$name' not provided")
        } else {
            null
        }
    }
}