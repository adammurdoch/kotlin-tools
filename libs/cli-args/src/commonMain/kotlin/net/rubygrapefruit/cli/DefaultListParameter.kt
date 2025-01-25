package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

internal class DefaultListParameter<T : Any>(
    private val name: String,
    private val help: String?,
    private val host: Host,
    private val owner: Action,
    private val default: List<T>,
    private val required: Boolean,
    private val converter: StringConverter<T>
) : Positional, ListParameter<T> {
    private val values = mutableListOf<T>()

    override fun whenAbsent(default: List<T>): Parameter<List<T>> {
        return owner.replace(this, DefaultListParameter(name, help, host, owner, default, false, converter))
    }

    override fun optional(): Parameter<List<T>> {
        return if (!required) {
            this
        } else {
            owner.replace(this, DefaultListParameter(name, help, host, owner, emptyList(), false, converter))
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
        return if (values.isNotEmpty()) values.toList() else default
    }

    override fun usage(): PositionalUsage {
        return ParameterUsage("<$name>...", "<$name>", help, converter.type, if (required) Cardinality.OneOrMore else Cardinality.ZeroOrMore, converter.candidateValues)
    }

    override fun usage(name: String): ActionUsage? {
        return null
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        for (index in args.indices) {
            val arg = args[index]
            if (host.isOption(arg)) {
                return ParseResult.Success(index)
            }
            val result = converter.convert("parameter '$name'", arg)
            when (result) {
                is StringConverter.Success -> values.add(result.value)
                is StringConverter.Failure -> return ParseResult.Failure(index, ArgParseException(result.message), expectedMore = true)
            }
        }
        return ParseResult.Success(args.size)
    }

    override fun canAcceptMore(): Boolean {
        return true
    }

    override fun finished(context: ParseContext): FinishResult {
        return if (required && values.isEmpty()) {
            FinishResult.Failure(ArgParseException("Parameter '$name' not provided"), expectedMore = true)
        } else {
            FinishResult.Success
        }
    }
}