package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal open class DefaultListParameter<T : Any>(
    protected val name: String,
    protected val help: String?,
    protected val host: Host,
    protected val owner: Action,
    private val default: List<T>,
    protected val required: Boolean,
    private val converter: StringConverter<T>
) : Positional(), ListParameter<T> {
    private val values = mutableListOf<T>()

    override fun whenAbsent(default: List<T>): Parameter<List<T>> {
        return owner.replace(this, DefaultListParameter(name, help, host, owner, default, required, converter))
    }

    override fun required(): Parameter<List<T>> {
        return if (required) {
            this
        } else {
            owner.replace(this, DefaultListParameter(name, help, host, owner, default, true, converter))
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
        return if (values.isNotEmpty()) values.toList() else default
    }

    override fun usage(): PositionalUsage {
        return ParameterUsage("<$name>...", "<$name>", help, converter.type, true)
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        for (index in args.indices) {
            val arg = args[index]
            if (host.isOption(arg)) {
                return ParseResult(index, null, false)
            }
            val converted = converter.convert("parameter '$name'", arg)
            if (converted.isFailure) {
                return ParseResult(index + 1, converted.exceptionOrNull() as ArgParseException, false)
            }
            values.add(converted.getOrThrow())
        }
        return ParseResult(args.size, null, false)
    }

    override fun missing(): ArgParseException? {
        return if (required && values.isEmpty()) {
            ArgParseException("Parameter '$name' not provided")
        } else {
            null
        }
    }
}