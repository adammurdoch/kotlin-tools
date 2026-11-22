package net.rubygrapefruit.cli.parser

internal object IdentityStringType : StringTypeInternal<String> {
    override fun unexpectedType(value: String): Problem? {
        return null
    }

    override fun convert(value: String): StringTypeInternal.Result<String> {
        return StringTypeInternal.Success(value)
    }
}
