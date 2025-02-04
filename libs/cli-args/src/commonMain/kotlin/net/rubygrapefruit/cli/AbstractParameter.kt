package net.rubygrapefruit.cli

internal abstract class AbstractParameter<T : Any>(
    val name: String,
    protected val help: String?,
    private val canBeMissing: Boolean,
    protected val host: Host,
    protected val converter: StringConverter<T>
) : Positional {
    private var set = false
    protected var value: T? = null
        private set

    protected open val usage
        get() = "<$name>"

    protected fun usage(cardinality: Cardinality): PositionalUsage {
        return ParameterUsage(usage, "<$name>", help, converter.type, cardinality, converter.candidateValues)
    }

    override fun usage(name: String): ActionUsage? {
        return null
    }

    override fun accept(args: List<String>, context: ParseContext): ParseResult {
        val candidate = args.first()
        return if (host.isOption(candidate)) {
            ParseResult.Nothing
        } else {
            val result = converter.convert("parameter '$name'", candidate)
            if (result is StringConverter.Success) {
                set = true
                value = result.value
                ParseResult.One
            } else if (canBeMissing) {
                set = true
                ParseResult.Nothing
            } else {
                result.asParseResult()
            }
        }
    }

    override fun canAcceptMore(): Boolean {
        return !set
    }

    fun value(value: T?) {
        set = true
        this.value = value
    }
}