package net.rubygrapefruit.cli.parser

internal class MappingType<OUT>(private val summary: String, private val mapping: (String) -> StringType.Result<OUT>) : StringTypeInternal<OUT> {
    override val expecting: List<String>
        get() = listOf(summary)

    override fun convert(value: String): StringType.Result<OUT> {
        return mapping(value)
    }
}
