package net.rubygrapefruit.cli

internal abstract class AbstractParameter<T : Any>(
    val name: String,
    protected val help: String?,
    protected val converter: StringConverter<T>
) : Positional {
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

    fun value(value: T?) {
        this.value = value
    }
}