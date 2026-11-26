package net.rubygrapefruit.cli.parser

internal object IdentityStringType : StringTypeInternal<String> {
    override val expecting: List<String>
        get() = emptyList()

    override fun convert(value: String): StringType.Result<String> {
        return StringType.Success(value)
    }
}
