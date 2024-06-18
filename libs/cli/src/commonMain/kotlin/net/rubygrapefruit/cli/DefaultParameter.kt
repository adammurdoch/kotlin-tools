package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal open class DefaultParameter<T : Any>(
    protected val name: String,
    protected val help: String?,
    protected val default: T?,
    protected val host: Host,
    protected val owner: Action,
    private val converter: StringConverter<T>
) : Positional(), Parameter<T> {
    private var value: T? = null

    override fun whenAbsent(default: T): Parameter<T> {
        return owner.replace(this, DefaultParameter(name, help, default, host, owner, converter))
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
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
            val result = converter.convert("parameter '$name'", candidate)
            if (result.isSuccess) {
                value = result.getOrThrow()
                ParseResult.One
            } else {
                ParseResult(1, result.exceptionOrNull() as ArgParseException, true)
            }
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