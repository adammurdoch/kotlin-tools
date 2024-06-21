package net.rubygrapefruit.cli

internal abstract class AbstractParameter<T : Any>(
    protected val name: String,
    protected val help: String?,
    private val canBeMissing: Boolean,
    protected val host: Host,
    private val converter: StringConverter<T>
) : Positional() {
    protected var value: T? = null

    protected fun usage(cardinality: Cardinality): PositionalUsage {
        return ParameterUsage("<$name>", "<$name>", help, converter.type, cardinality)
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
            } else if (canBeMissing) {
                ParseResult(0, null, true)
            } else {
                ParseResult(1, result.exceptionOrNull() as ArgParseException, true)
            }
        }
    }
}