package net.rubygrapefruit.cli.parser

internal object IdentityStringType : StringTypeInternal<String> {
    override val expecting: String?
        get() = null

    override fun convert(value: String): StringTypeInternal.Result<String> {
        return StringTypeInternal.Success(value)
    }
}
