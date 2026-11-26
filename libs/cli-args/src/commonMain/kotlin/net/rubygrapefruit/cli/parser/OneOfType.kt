package net.rubygrapefruit.cli.parser

internal class OneOfType<out OUT>(private val values: Map<String, OUT>) : StringTypeInternal<OUT> {
    override val expecting: List<String>
        get() = values.keys.map { "'$it'" }

    override fun convert(value: String): StringType.Result<OUT> {
        val result = values[value]
        return if (result != null) {
            StringType.Success(result)
        } else {
            StringType.Failure
        }
    }
}
